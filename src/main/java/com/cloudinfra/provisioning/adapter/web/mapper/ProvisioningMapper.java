package com.cloudinfra.provisioning.adapter.web.mapper;

import com.cloudinfra.provisioning.adapter.web.dto.CloudCredentialsDTO;
import com.cloudinfra.provisioning.adapter.web.dto.InfrastructureInstructionsDTO;
import com.cloudinfra.provisioning.adapter.web.dto.ProvisioningRequestDTO;
import com.cloudinfra.provisioning.domain.model.CloudCredentials;
import com.cloudinfra.provisioning.domain.model.InfrastructureInstructions;
import com.cloudinfra.provisioning.domain.model.ProvisioningRequest;
import org.springframework.stereotype.Component;

@Component
public class ProvisioningMapper {

    public ProvisioningRequest toDomain(ProvisioningRequestDTO dto) {
        return ProvisioningRequest.builder()
            .cloudProvider(dto.getCloudProvider())
            .aiModel(dto.getAiModel())
            .infrastructureDescription(dto.getInfrastructureDescription())
            .credentials(mapCredentials(dto.getCredentials(), dto.getCloudProvider()))
            .additionalParameters(dto.getAdditionalParameters())
            .build();
    }

    public InfrastructureInstructionsDTO toDTO(InfrastructureInstructions domain) {
        return InfrastructureInstructionsDTO.builder()
            .requestId(domain.getRequestId())
            .cloudProvider(domain.getCloudProvider())
            .steps(domain.getSteps())
            .componentCommands(domain.getComponentCommands())
            .estimatedDuration(domain.getEstimatedDuration())
            .estimatedCost(domain.getEstimatedCost())
            .build();
    }

    private CloudCredentials mapCredentials(CloudCredentialsDTO dto, com.cloudinfra.provisioning.domain.model.CloudProvider provider) {
        return CloudCredentials.builder()
            .provider(provider)
            .accessKey(dto.getAccessKey())
            .secretKey(dto.getSecretKey())
            .region(dto.getRegion())
            .projectId(dto.getProjectId())
            .subscriptionId(dto.getSubscriptionId())
            .tenantId(dto.getTenantId())
            .additionalCredentials(dto.getAdditionalCredentials())
            .build();
    }
}
