package com.cloudinfra.provisioning.domain.model;

public enum AIModel {
    OPENAI("openAIProvider"),
    CLAUDE("claudeProvider"),
    GEMINI("geminiProvider");

    private final String providerBeanName;

    AIModel(String providerBeanName) {
        this.providerBeanName = providerBeanName;
    }

    public String getProviderBeanName() {
        return providerBeanName;
    }
}
