package com.cloudinfra.provisioning.adapter.web.controller;

import com.cloudinfra.provisioning.adapter.web.dto.InfrastructureInstructionsDTO;
import com.cloudinfra.provisioning.adapter.web.dto.ProvisioningRequestDTO;
import com.cloudinfra.provisioning.adapter.web.mapper.ProvisioningMapper;
import com.cloudinfra.provisioning.domain.model.InfrastructureInstructions;
import com.cloudinfra.provisioning.domain.model.ProvisioningRequest;
import com.cloudinfra.provisioning.domain.ports.in.GenerateInfrastructureUseCase;
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

    private final GenerateInfrastructureUseCase generateInfrastructureUseCase;
    private final ProvisioningMapper provisioningMapper;

    @PostMapping("/provision")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<InfrastructureInstructionsDTO> generateProvisioningInstructions(
        @Valid @RequestBody ProvisioningRequestDTO requestDTO
    ) {
        log.info("Received provisioning request for {} using {}",
            requestDTO.getCloudProvider(),
            requestDTO.getAiModel()
        );

        ProvisioningRequest request = provisioningMapper.toDomain(requestDTO);

        InfrastructureInstructions instructions = generateInfrastructureUseCase.generateInstructions(request);

        InfrastructureInstructionsDTO responseDTO = provisioningMapper.toDTO(instructions);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
}
