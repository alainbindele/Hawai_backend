package com.cloudinfra.provisioning.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationCommand {
    private String componentName;
    private String commandType;
    private String command;
    private String description;
    private int executionOrder;
}
