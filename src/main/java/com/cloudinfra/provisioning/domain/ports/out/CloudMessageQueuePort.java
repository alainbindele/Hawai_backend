package com.cloudinfra.provisioning.domain.ports.out;

import com.cloudinfra.provisioning.domain.model.CloudProvider;
import com.cloudinfra.provisioning.domain.model.InfrastructureInstructions;

public interface CloudMessageQueuePort {
    void sendInstructions(CloudProvider cloudProvider, InfrastructureInstructions instructions);
}
