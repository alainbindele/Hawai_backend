package com.cloudinfra.provisioning.domain.ports.out;

import com.cloudinfra.provisioning.domain.model.ConversationHistory;

import java.util.Optional;

public interface ConversationRepositoryPort {
    ConversationHistory save(ConversationHistory conversation);
    Optional<ConversationHistory> findById(String conversationId);
    void deleteById(String conversationId);
}
