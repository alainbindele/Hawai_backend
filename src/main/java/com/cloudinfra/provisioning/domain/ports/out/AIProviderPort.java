package com.cloudinfra.provisioning.domain.ports.out;

import com.cloudinfra.provisioning.domain.model.AIModel;
import com.cloudinfra.provisioning.domain.model.CloudProvider;
import com.cloudinfra.provisioning.domain.model.ConversationHistory;

import java.util.List;

public interface AIProviderPort {
    String generateInfrastructureInstructions(
        AIModel aiModel,
        CloudProvider cloudProvider,
        String infrastructureDescription,
        String additionalContext,
        List<com.ai.Message> conversationHistory
    );
}
