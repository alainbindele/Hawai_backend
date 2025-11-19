package com.cloudinfra.provisioning.adapter.web.dto;

import com.cloudinfra.provisioning.domain.model.AIResponseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIResponseDTO {
    private AIResponseType type;
    private InfrastructureInstructionsDTO instructions;
    private FormRequestDTO formRequest;
}
