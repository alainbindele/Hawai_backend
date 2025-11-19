package com.cloudinfra.provisioning.domain.model;

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
public class InfrastructureInstructions {
    private String requestId;
    private CloudProvider cloudProvider;
    private List<ProvisioningStep> steps;
    private Map<String, List<ConfigurationCommand>> componentCommands;
    private String estimatedDuration;
    private String estimatedCost;
}
