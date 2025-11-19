package com.cloudinfra.provisioning.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationHistory {
    private String conversationId;
    private String requestId;
    private CloudProvider cloudProvider;
    private AIModel aiModel;
    private CloudCredentials credentials;

    @Builder.Default
    private List<ConversationMessage> messages = new ArrayList<>();

    private ConversationStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
