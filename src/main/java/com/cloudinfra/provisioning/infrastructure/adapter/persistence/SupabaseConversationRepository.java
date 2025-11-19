package com.cloudinfra.provisioning.infrastructure.adapter.persistence;

import com.cloudinfra.provisioning.domain.model.ConversationHistory;
import com.cloudinfra.provisioning.domain.ports.out.ConversationRepositoryPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SupabaseConversationRepository implements ConversationRepositoryPort {

    private final ObjectMapper objectMapper;
    private final Map<String, ConversationHistory> inMemoryCache = new ConcurrentHashMap<>();

    @Override
    public ConversationHistory save(ConversationHistory conversation) {
        log.info("Saving conversation: {}", conversation.getConversationId());

        inMemoryCache.put(conversation.getConversationId(), conversation);

        return conversation;
    }

    @Override
    public Optional<ConversationHistory> findById(String conversationId) {
        log.info("Finding conversation: {}", conversationId);

        ConversationHistory conversation = inMemoryCache.get(conversationId);
        return Optional.ofNullable(conversation);
    }

    @Override
    public void deleteById(String conversationId) {
        log.info("Deleting conversation: {}", conversationId);
        inMemoryCache.remove(conversationId);
    }
}
