package com.cloudinfra.provisioning.infrastructure.adapter.ai;

import com.cloudinfra.provisioning.domain.model.CloudProvider;
import org.springframework.stereotype.Component;

@Component
public class AIPromptBuilder {

    public String buildSystemPrompt(CloudProvider cloudProvider) {
        return String.format("""
            You are an expert cloud infrastructure architect specialized in %s.

            Your task is to generate detailed infrastructure provisioning instructions in JSON format.

            The JSON response must follow this exact structure:
            {
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

            Rules:
            1. Provide complete, executable commands for %s CLI
            2. Include error handling and validation steps
            3. For EC2/VMs, include SSM/SSH configuration commands
            4. Specify security groups, IAM roles, and networking details
            5. Include monitoring and logging setup
            6. Return ONLY valid JSON, no markdown formatting
            7. Be specific with resource names, IDs, and configurations
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
