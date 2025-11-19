package com.cloudinfra.provisioning.adapter.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloudCredentialsDTO {

    private String accessKey;

    private String secretKey;

    @NotBlank(message = "Region is required")
    private String region;

    private String projectId;

    private String subscriptionId;

    private String tenantId;

    private Map<String, String> additionalCredentials;
}
