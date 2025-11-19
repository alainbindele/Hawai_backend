package com.cloudinfra.provisioning.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProvisioningRequest {
    private String id;
    private CloudProvider cloudProvider;
    private AIModel aiModel;
    private String infrastructureDescription;
    private CloudCredentials credentials;
    private Map<String, String> additionalParameters;
    private Instant createdAt;
    private ProvisioningStatus status;
}
