package com.cloudinfra.provisioning.infrastructure.adapter.persistence;

import com.cloudinfra.provisioning.domain.model.ProvisioningRequest;
import com.cloudinfra.provisioning.domain.ports.out.ProvisioningRepositoryPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class InMemoryProvisioningRepository implements ProvisioningRepositoryPort {

    private final Map<String, ProvisioningRequest> storage = new ConcurrentHashMap<>();

    @Override
    public ProvisioningRequest save(ProvisioningRequest request) {
        log.debug("Saving provisioning request: {}", request.getId());
        storage.put(request.getId(), request);
        return request;
    }

    @Override
    public Optional<ProvisioningRequest> findById(String id) {
        log.debug("Finding provisioning request: {}", id);
        return Optional.ofNullable(storage.get(id));
    }
}
