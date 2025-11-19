package com.cloudinfra.provisioning.infrastructure.adapter.ai;

import com.cloudinfra.provisioning.domain.model.CloudProvider;
import org.springframework.stereotype.Component;

@Component
public class AIPromptBuilder {

    public String buildSystemPrompt(CloudProvider cloudProvider) {
        return String.format("""
            You are an expert cloud infrastructure architect specialized in %s.

            Your task is to analyze infrastructure requirements and respond in ONE of two ways:

            OPTION 1 - If you have SUFFICIENT information, generate provisioning instructions:
            {
              "type": "PROVISIONING_INSTRUCTIONS",
              "steps": [
                {
                  "order": 1,
                  "stepName": "Create VPC",
                  "description": "Detailed description of the step",
                  "service": "VPC",
                  "commands": ["command1", "command2"],
                  "expectedOutput": "Expected result",
                  "errorHandling": "How to handle errors"
                }
              ],
              "componentCommands": {
                "ec2-webserver-1": [
                  {
                    "componentName": "ec2-webserver-1",
                    "commandType": "bash",
                    "command": "sudo apt-get update",
                    "description": "Update package lists",
                    "executionOrder": 1
                  }
                ]
              },
              "estimatedDuration": "30 minutes",
              "estimatedCost": "$50/month"
            }

            OPTION 2 - If you need MORE information, request it with a dynamic form:
            {
              "type": "MORE_INFO_REQUIRED",
              "message": "I need additional information to generate accurate provisioning instructions.",
              "fields": [
                {
                  "name": "instanceType",
                  "label": "EC2 Instance Type",
                  "type": "select",
                  "required": true,
                  "description": "The type of EC2 instance to provision",
                  "options": ["t2.micro", "t3.medium", "m5.large"],
                  "placeholder": "Select instance type"
                },
                {
                  "name": "storageSize",
                  "label": "Storage Size (GB)",
                  "type": "number",
                  "required": true,
                  "description": "Amount of storage in GB",
                  "placeholder": "Enter storage size"
                }
              ]
            }

            Rules:
            1. ALWAYS include "type" field: either "PROVISIONING_INSTRUCTIONS" or "MORE_INFO_REQUIRED"
            2. For provisioning: provide complete, executable commands for %s CLI
            3. For more info: create appropriate form fields (text, number, select, textarea, checkbox)
            4. Return ONLY valid JSON, no markdown formatting
            5. Be specific and detailed in your requirements
            """,
            cloudProvider.name(),
            getProviderCLI(cloudProvider)
        );
    }

    public String buildUserMessage(String infrastructureDescription, String additionalContext) {
        return String.format("""
            Generate infrastructure provisioning instructions for the following requirements:

            Infrastructure Description:
            %s

            Additional Context:
            %s

            Please provide detailed, step-by-step instructions with all necessary commands and configurations.
            """,
            infrastructureDescription,
            additionalContext
        );
    }

    private String getProviderCLI(CloudProvider provider) {
        return switch (provider) {
            case AWS -> "AWS CLI";
            case GOOGLE_CLOUD -> "gcloud CLI";
            case AZURE -> "Azure CLI";
        };
    }
}
