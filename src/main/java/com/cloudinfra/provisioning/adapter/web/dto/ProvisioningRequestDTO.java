package com.cloudinfra.provisioning.adapter.web.dto;

import com.cloudinfra.provisioning.domain.model.AIModel;
import com.cloudinfra.provisioning.domain.model.CloudProvider;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProvisioningRequestDTO {

    @NotNull(message = "Cloud provider is required")
    private CloudProvider cloudProvider;

    @NotNull(message = "AI model is required")
    private AIModel aiModel;

    @NotBlank(message = "Infrastructure description is required")
    private String infrastructureDescription;

    @Valid
    @NotNull(message = "Credentials are required")
    private CloudCredentialsDTO credentials;

    private Map<String, String> additionalParameters;
}
