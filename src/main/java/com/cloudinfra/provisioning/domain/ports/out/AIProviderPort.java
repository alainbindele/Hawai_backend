package com.cloudinfra.provisioning.domain.ports.out;

import com.cloudinfra.provisioning.domain.model.AIModel;
import com.cloudinfra.provisioning.domain.model.CloudProvider;

public interface AIProviderPort {
    String generateInfrastructureInstructions(
        AIModel aiModel,
        CloudProvider cloudProvider,
        String infrastructureDescription,
        String additionalContext
    );
}
