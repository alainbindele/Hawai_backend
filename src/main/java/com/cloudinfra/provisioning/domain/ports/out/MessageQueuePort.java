package com.cloudinfra.provisioning.domain.ports.out;

import com.cloudinfra.provisioning.domain.model.InfrastructureInstructions;

public interface MessageQueuePort {
    void sendInstructions(InfrastructureInstructions instructions);
}
