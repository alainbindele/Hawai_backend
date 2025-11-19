package com.cloudinfra.provisioning.infrastructure.adapter.queue;

import com.cloudinfra.provisioning.domain.model.CloudProvider;
import com.cloudinfra.provisioning.domain.model.InfrastructureInstructions;
import com.cloudinfra.provisioning.domain.ports.out.CloudMessageQueuePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class CloudMessageQueueAdapter implements CloudMessageQueuePort {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.queue-url:}")
    private String awsQueueUrl;

    @Value("${gcp.queue-url:}")
    private String gcpQueueUrl;

    @Value("${azure.queue-url:}")
    private String azureQueueUrl;

    @Override
    public void sendInstructions(CloudProvider cloudProvider, InfrastructureInstructions instructions) {
        switch (cloudProvider) {
            case AWS -> sendToAWS(instructions);
            case GOOGLE_CLOUD -> sendToGCP(instructions);
            case AZURE -> sendToAzure(instructions);
        }
    }

    private void sendToAWS(InfrastructureInstructions instructions) {
        try {
            String messageBody = objectMapper.writeValueAsString(instructions);

            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                .queueUrl(awsQueueUrl)
                .messageBody(messageBody)
                .messageGroupId(instructions.getCloudProvider().name())
                .messageDeduplicationId(instructions.getRequestId())
                .build();

            SendMessageResponse response = sqsClient.sendMessage(sendMessageRequest);

            log.info("Message sent to AWS SQS. MessageId: {}, RequestId: {}",
                response.messageId(),
                instructions.getRequestId()
            );

        } catch (Exception e) {
            log.error("Error sending message to AWS SQS", e);
            throw new RuntimeException("Failed to send message to AWS SQS", e);
        }
    }

    private void sendToGCP(InfrastructureInstructions instructions) {
        try {
            String messageBody = objectMapper.writeValueAsString(instructions);

            log.info("Message would be sent to GCP Pub/Sub. Queue: {}, RequestId: {}",
                gcpQueueUrl,
                instructions.getRequestId()
            );

            log.warn("GCP Pub/Sub integration not fully implemented. Message logged only.");

        } catch (Exception e) {
            log.error("Error sending message to GCP Pub/Sub", e);
            throw new RuntimeException("Failed to send message to GCP Pub/Sub", e);
        }
    }

    private void sendToAzure(InfrastructureInstructions instructions) {
        try {
            String messageBody = objectMapper.writeValueAsString(instructions);

            log.info("Message would be sent to Azure Service Bus. Queue: {}, RequestId: {}",
                azureQueueUrl,
                instructions.getRequestId()
            );

            log.warn("Azure Service Bus integration not fully implemented. Message logged only.");

        } catch (Exception e) {
            log.error("Error sending message to Azure Service Bus", e);
            throw new RuntimeException("Failed to send message to Azure Service Bus", e);
        }
    }
}
