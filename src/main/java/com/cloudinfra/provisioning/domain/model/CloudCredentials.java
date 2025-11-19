package com.cloudinfra.provisioning.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloudCredentials {
    private CloudProvider provider;
    private String accessKey;
    private String secretKey;
    private String region;
    private String projectId;
    private String subscriptionId;
    private String tenantId;
    private Map<String, String> additionalCredentials;
}
