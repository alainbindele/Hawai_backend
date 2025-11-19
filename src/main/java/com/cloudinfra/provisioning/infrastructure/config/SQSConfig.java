package com.cloudinfra.provisioning.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class SQSConfig {

    @Value("${aws.sqs.access-key:}")
    private String accessKey;

    @Value("${aws.sqs.secret-key:}")
    private String secretKey;

    @Value("${aws.sqs.region:us-east-1}")
    private String region;

    @Bean
    public SqsClient sqsClient() {
        if (accessKey != null && !accessKey.isEmpty() && secretKey != null && !secretKey.isEmpty()) {
            AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
            return SqsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
        }

        return SqsClient.builder()
            .region(Region.of(region))
            .build();
    }
}
