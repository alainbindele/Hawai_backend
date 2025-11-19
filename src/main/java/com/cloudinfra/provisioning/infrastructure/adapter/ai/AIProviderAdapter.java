package com.cloudinfra.provisioning.infrastructure.adapter.ai;

import com.cloudinfra.provisioning.domain.model.AIModel;
import com.cloudinfra.provisioning.domain.model.CloudProvider;
import com.cloudinfra.provisioning.domain.ports.out.AIProviderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AIProviderAdapter implements AIProviderPort {

    private final BeanFactory beanFactory;
    private final AIPromptBuilder promptBuilder;

    @Override
    public String generateInfrastructureInstructions(
        AIModel aiModel,
        CloudProvider cloudProvider,
        String infrastructureDescription,
        String additionalContext,
        List<com.ai.Message> conversationHistory
    ) {
        try {
            log.info("Using AI model: {} for cloud provider: {}", aiModel, cloudProvider);

            Object providerBean = beanFactory.getBean(aiModel.getProviderBeanName());

            if (providerBean == null) {
                throw new IllegalStateException("AI Provider bean not found: " + aiModel.getProviderBeanName());
            }

            String systemPrompt = promptBuilder.buildSystemPrompt(cloudProvider);
            String userMessage = promptBuilder.buildUserMessage(infrastructureDescription, additionalContext);

            String response;
            if (providerBean instanceof com.ai.AIProvider) {
                com.ai.AIProvider aiProvider = (com.ai.AIProvider) providerBean;
                response = aiProvider.sendMessage(userMessage, conversationHistory, systemPrompt);
            } else {
                throw new IllegalStateException("Provider bean is not an instance of AIProvider");
            }

            log.info("AI response generated successfully");
            return response;

        } catch (Exception e) {
            log.error("Error calling AI provider", e);
            throw new RuntimeException("Failed to generate AI response", e);
        }
    }
}
