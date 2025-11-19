package com.cloudinfra.provisioning.infrastructure.adapter.queue;

import com.cloudinfra.provisioning.domain.model.InfrastructureInstructions;
import com.cloudinfra.provisioning.domain.ports.out.MessageQueuePort;
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
public class SQSMessageQueueAdapter implements MessageQueuePort {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    @Override
    public void sendInstructions(InfrastructureInstructions instructions) {
        try {
            String messageBody = objectMapper.writeValueAsString(instructions);

            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(messageBody)
                .messageGroupId(instructions.getCloudProvider().name())
                .messageDeduplicationId(instructions.getRequestId())
                .build();

            SendMessageResponse response = sqsClient.sendMessage(sendMessageRequest);

            log.info("Message sent to SQS. MessageId: {}, RequestId: {}",
                response.messageId(),
                instructions.getRequestId()
            );

        } catch (Exception e) {
            log.error("Error sending message to SQS", e);
            throw new RuntimeException("Failed to send message to SQS", e);
        }
    }
}
