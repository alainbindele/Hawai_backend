package com.cloudinfra.provisioning.application.service;

import com.cloudinfra.provisioning.domain.model.*;
import com.cloudinfra.provisioning.domain.ports.out.AIProviderPort;
import com.cloudinfra.provisioning.domain.ports.out.CloudMessageQueuePort;
import com.cloudinfra.provisioning.domain.ports.out.ConversationRepositoryPort;
import com.cloudinfra.provisioning.domain.ports.out.ProvisioningRepositoryPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationalProvisioningService {

    private final AIProviderPort aiProviderPort;
    private final CloudMessageQueuePort cloudMessageQueuePort;
    private final ConversationRepositoryPort conversationRepositoryPort;
    private final ProvisioningRepositoryPort provisioningRepositoryPort;
    private final ObjectMapper objectMapper;

    public AIResponse initiateProvisioning(ProvisioningRequest request) {
        try {
            request.setId(UUID.randomUUID().toString());
            request.setCreatedAt(Instant.now());
            request.setStatus(ProvisioningStatus.PENDING);
            provisioningRepositoryPort.save(request);

            ConversationHistory conversation = ConversationHistory.builder()
                .conversationId(UUID.randomUUID().toString())
                .requestId(request.getId())
                .cloudProvider(request.getCloudProvider())
                .aiModel(request.getAiModel())
                .credentials(request.getCredentials())
                .messages(new ArrayList<>())
                .status(ConversationStatus.AWAITING_MORE_INFO)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

            String additionalContext = buildAdditionalContext(request);

            conversation.getMessages().add(ConversationMessage.builder()
                .role("user")
                .content("Infrastructure Description: " + request.getInfrastructureDescription() + "\n" + additionalContext)
                .timestamp(Instant.now())
                .build());

            String aiResponse = aiProviderPort.generateInfrastructureInstructions(
                request.getAiModel(),
                request.getCloudProvider(),
                request.getInfrastructureDescription(),
                additionalContext,
                convertToAIMessages(conversation.getMessages())
            );

            conversation.getMessages().add(ConversationMessage.builder()
                .role("assistant")
                .content(aiResponse)
                .timestamp(Instant.now())
                .build());

            conversation.setUpdatedAt(Instant.now());
            conversationRepositoryPort.save(conversation);

            return parseAIResponse(aiResponse, conversation);

        } catch (Exception e) {
            log.error("Error initiating provisioning", e);
            request.setStatus(ProvisioningStatus.FAILED);
            provisioningRepositoryPort.save(request);
            throw new RuntimeException("Failed to initiate provisioning", e);
        }
    }

    public AIResponse continueConversation(String conversationId, Map<String, String> formData) {
        try {
            ConversationHistory conversation = conversationRepositoryPort.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found: " + conversationId));

            String userMessage = buildUserMessageFromFormData(formData);

            conversation.getMessages().add(ConversationMessage.builder()
                .role("user")
                .content(userMessage)
                .timestamp(Instant.now())
                .build());

            String additionalContext = buildAdditionalContextFromConversation(conversation, formData);

            String aiResponse = aiProviderPort.generateInfrastructureInstructions(
                conversation.getAiModel(),
                conversation.getCloudProvider(),
                "Continue with the previously discussed infrastructure requirements",
                additionalContext,
                convertToAIMessages(conversation.getMessages())
            );

            conversation.getMessages().add(ConversationMessage.builder()
                .role("assistant")
                .content(aiResponse)
                .timestamp(Instant.now())
                .build());

            conversation.setUpdatedAt(Instant.now());

            AIResponse response = parseAIResponse(aiResponse, conversation);

            if (response.getType() == AIResponseType.PROVISIONING_INSTRUCTIONS) {
                conversation.setStatus(ConversationStatus.READY_TO_PROVISION);
                conversationRepositoryPort.save(conversation);

                Optional<ProvisioningRequest> optionalRequest = provisioningRepositoryPort.findById(conversation.getRequestId());
                if (optionalRequest.isPresent()) {
                    ProvisioningRequest request = optionalRequest.get();
                    request.setStatus(ProvisioningStatus.QUEUED);
                    provisioningRepositoryPort.save(request);
                }

                log.info("Sending instructions to message queue");
                cloudMessageQueuePort.sendInstructions(
                    conversation.getCloudProvider(),
                    response.getInstructions()
                );

                conversation.setStatus(ConversationStatus.COMPLETED);
            }

            conversationRepositoryPort.save(conversation);

            return response;

        } catch (Exception e) {
            log.error("Error continuing conversation", e);
            throw new RuntimeException("Failed to continue conversation", e);
        }
    }

    private AIResponse parseAIResponse(String aiResponse, ConversationHistory conversation) {
        try {
            String jsonContent = extractJsonFromResponse(aiResponse);
            JsonNode rootNode = objectMapper.readTree(jsonContent);

            String typeStr = rootNode.has("type") ? rootNode.get("type").asText() : "PROVISIONING_INSTRUCTIONS";
            AIResponseType type = AIResponseType.valueOf(typeStr);

            if (type == AIResponseType.MORE_INFO_REQUIRED) {
                FormRequest formRequest = objectMapper.treeToValue(rootNode, FormRequest.class);
                formRequest.setConversationId(conversation.getConversationId());

                return AIResponse.builder()
                    .type(AIResponseType.MORE_INFO_REQUIRED)
                    .rawResponse(aiResponse)
                    .formRequest(formRequest)
                    .build();
            } else {
                InfrastructureInstructions instructions = objectMapper.treeToValue(rootNode, InfrastructureInstructions.class);
                instructions.setRequestId(conversation.getRequestId());
                instructions.setCloudProvider(conversation.getCloudProvider());

                return AIResponse.builder()
                    .type(AIResponseType.PROVISIONING_INSTRUCTIONS)
                    .rawResponse(aiResponse)
                    .instructions(instructions)
                    .build();
            }

        } catch (Exception e) {
            log.error("Error parsing AI response", e);
            throw new RuntimeException("Failed to parse AI response", e);
        }
    }

    private String extractJsonFromResponse(String response) {
        response = response.trim();

        if (response.contains("```json")) {
            int startIdx = response.indexOf("```json") + 7;
            int endIdx = response.indexOf("```", startIdx);
            if (endIdx > startIdx) {
                response = response.substring(startIdx, endIdx).trim();
            }
        }

        int firstBrace = response.indexOf('{');
        int lastBrace = response.lastIndexOf('}');

        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            response = response.substring(firstBrace, lastBrace + 1);
        }

        return response;
    }

    private String buildAdditionalContext(ProvisioningRequest request) {
        StringBuilder context = new StringBuilder();
        context.append("Cloud Provider: ").append(request.getCloudProvider()).append("\n");
        context.append("Region: ").append(request.getCredentials().getRegion()).append("\n");

        if (request.getAdditionalParameters() != null && !request.getAdditionalParameters().isEmpty()) {
            context.append("Additional Parameters:\n");
            request.getAdditionalParameters().forEach((key, value) ->
                context.append("- ").append(key).append(": ").append(value).append("\n")
            );
        }

        return context.toString();
    }

    private String buildAdditionalContextFromConversation(ConversationHistory conversation, Map<String, String> formData) {
        StringBuilder context = new StringBuilder();
        context.append("Cloud Provider: ").append(conversation.getCloudProvider()).append("\n");
        context.append("Region: ").append(conversation.getCredentials().getRegion()).append("\n");

        if (formData != null && !formData.isEmpty()) {
            context.append("User Provided Data:\n");
            formData.forEach((key, value) ->
                context.append("- ").append(key).append(": ").append(value).append("\n")
            );
        }

        return context.toString();
    }

    private String buildUserMessageFromFormData(Map<String, String> formData) {
        StringBuilder message = new StringBuilder("Here is the additional information you requested:\n\n");
        formData.forEach((key, value) ->
            message.append(key).append(": ").append(value).append("\n")
        );
        return message.toString();
    }

    private List<com.ai.Message> convertToAIMessages(List<ConversationMessage> messages) {
        List<com.ai.Message> aiMessages = new ArrayList<>();
        for (ConversationMessage msg : messages) {
            aiMessages.add(new com.ai.Message(msg.getRole(), msg.getContent()));
        }
        return aiMessages;
    }
}
