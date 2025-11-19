package com.cloudinfra.provisioning.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIResponse {
    private AIResponseType type;
    private String rawResponse;
    private InfrastructureInstructions instructions;
    private FormRequest formRequest;
}
