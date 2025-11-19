package com.cloudinfra.provisioning.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProvisioningStep {
    private int order;
    private String stepName;
    private String description;
    private String service;
    private List<String> commands;
    private String expectedOutput;
    private String errorHandling;
}
