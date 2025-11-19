package com.cloudinfra.provisioning.domain.model;

public enum ProvisioningStatus {
    PENDING,
    GENERATING_INSTRUCTIONS,
    QUEUED,
    PROCESSING,
    COMPLETED,
    FAILED
}
