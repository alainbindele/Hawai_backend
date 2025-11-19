package com.cloudinfra.provisioning.domain.ports.in;

import com.cloudinfra.provisioning.domain.model.InfrastructureInstructions;
import com.cloudinfra.provisioning.domain.model.ProvisioningRequest;

public interface GenerateInfrastructureUseCase {
    InfrastructureInstructions generateInstructions(ProvisioningRequest request);
}
