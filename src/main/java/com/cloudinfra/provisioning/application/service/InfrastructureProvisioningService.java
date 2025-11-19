package com.cloudinfra.provisioning.application.service;

import com.cloudinfra.provisioning.domain.model.InfrastructureInstructions;
import com.cloudinfra.provisioning.domain.model.ProvisioningRequest;
import com.cloudinfra.provisioning.domain.model.ProvisioningStatus;
import com.cloudinfra.provisioning.domain.ports.in.GenerateInfrastructureUseCase;
import com.cloudinfra.provisioning.domain.ports.out.AIProviderPort;
import com.cloudinfra.provisioning.domain.ports.out.MessageQueuePort;
import com.cloudinfra.provisioning.domain.ports.out.ProvisioningRepositoryPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfrastructureProvisioningService implements GenerateInfrastructureUseCase {

    private final AIProviderPort aiProviderPort;
    private final MessageQueuePort messageQueuePort;
    private final ProvisioningRepositoryPort provisioningRepositoryPort;
    private final ObjectMapper objectMapper;

    @Override
    public InfrastructureInstructions generateInstructions(ProvisioningRequest request) {
        try {
            request.setId(UUID.randomUUID().toString());
            request.setCreatedAt(Instant.now());
            request.setStatus(ProvisioningStatus.PENDING);

            provisioningRepositoryPort.save(request);

            log.info("Generating infrastructure instructions for request: {}", request.getId());
            request.setStatus(ProvisioningStatus.GENERATING_INSTRUCTIONS);
            provisioningRepositoryPort.save(request);

            String additionalContext = buildAdditionalContext(request);

            String aiResponse = aiProviderPort.generateInfrastructureInstructions(
                request.getAiModel(),
                request.getCloudProvider(),
                request.getInfrastructureDescription(),
                additionalContext
            );

            log.info("AI response received, parsing instructions");
            InfrastructureInstructions instructions = parseAIResponse(aiResponse, request);

            request.setStatus(ProvisioningStatus.QUEUED);
            provisioningRepositoryPort.save(request);

            log.info("Sending instructions to message queue");
            messageQueuePort.sendInstructions(instructions);

            return instructions;

        } catch (Exception e) {
            log.error("Error generating infrastructure instructions", e);
            request.setStatus(ProvisioningStatus.FAILED);
            provisioningRepositoryPort.save(request);
            throw new RuntimeException("Failed to generate infrastructure instructions", e);
        }
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

    private InfrastructureInstructions parseAIResponse(String aiResponse, ProvisioningRequest request) {
        try {
            String jsonContent = extractJsonFromResponse(aiResponse);

            InfrastructureInstructions instructions = objectMapper.readValue(
                jsonContent,
                InfrastructureInstructions.class
            );

            instructions.setRequestId(request.getId());
            instructions.setCloudProvider(request.getCloudProvider());

            return instructions;
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
}
