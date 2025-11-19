package com.cloudinfra.provisioning.adapter.web.dto;

import com.cloudinfra.provisioning.domain.model.CloudProvider;
import com.cloudinfra.provisioning.domain.model.ConfigurationCommand;
import com.cloudinfra.provisioning.domain.model.ProvisioningStep;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfrastructureInstructionsDTO {
    private String requestId;
    private CloudProvider cloudProvider;
    private List<ProvisioningStep> steps;
    private Map<String, List<ConfigurationCommand>> componentCommands;
    private String estimatedDuration;
    private String estimatedCost;
}
