package com.cloudinfra.provisioning.adapter.web.controller;

import com.cloudinfra.provisioning.adapter.web.dto.*;
import com.cloudinfra.provisioning.adapter.web.mapper.ProvisioningMapper;
import com.cloudinfra.provisioning.application.service.ConversationalProvisioningService;
import com.cloudinfra.provisioning.domain.model.AIResponse;
import com.cloudinfra.provisioning.domain.model.AIResponseType;
import com.cloudinfra.provisioning.domain.model.ProvisioningRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/infrastructure")
@RequiredArgsConstructor
public class InfrastructureProvisioningController {

    private final ConversationalProvisioningService conversationalProvisioningService;
    private final ProvisioningMapper provisioningMapper;

    @PostMapping("/provision")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AIResponseDTO> generateProvisioningInstructions(
        @Valid @RequestBody ProvisioningRequestDTO requestDTO
    ) {
        log.info("Received provisioning request for {} using {}",
            requestDTO.getCloudProvider(),
            requestDTO.getAiModel()
        );

        ProvisioningRequest request = provisioningMapper.toDomain(requestDTO);

        AIResponse aiResponse = conversationalProvisioningService.initiateProvisioning(request);

        AIResponseDTO responseDTO = mapAIResponse(aiResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PostMapping("/form/data-integration")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AIResponseDTO> submitFormData(
        @Valid @RequestBody FormDataDTO formDataDTO
    ) {
        log.info("Received form data for conversation: {}", formDataDTO.getConversationId());

        AIResponse aiResponse = conversationalProvisioningService.continueConversation(
            formDataDTO.getConversationId(),
            formDataDTO.getData()
        );

        AIResponseDTO responseDTO = mapAIResponse(aiResponse);

        return ResponseEntity.ok(responseDTO);
    }

    private AIResponseDTO mapAIResponse(AIResponse aiResponse) {
        AIResponseDTO dto = AIResponseDTO.builder()
            .type(aiResponse.getType())
            .build();

        if (aiResponse.getType() == AIResponseType.PROVISIONING_INSTRUCTIONS) {
            dto.setInstructions(provisioningMapper.toDTO(aiResponse.getInstructions()));
        } else if (aiResponse.getType() == AIResponseType.MORE_INFO_REQUIRED) {
            dto.setFormRequest(FormRequestDTO.builder()
                .conversationId(aiResponse.getFormRequest().getConversationId())
                .message(aiResponse.getFormRequest().getMessage())
                .fields(aiResponse.getFormRequest().getFields())
                .build());
        }

        return dto;
    }
}
