package com.cloudinfra.provisioning.domain.ports.out;

import com.cloudinfra.provisioning.domain.model.ProvisioningRequest;

import java.util.Optional;

public interface ProvisioningRepositoryPort {
    ProvisioningRequest save(ProvisioningRequest request);
    Optional<ProvisioningRequest> findById(String id);
}
