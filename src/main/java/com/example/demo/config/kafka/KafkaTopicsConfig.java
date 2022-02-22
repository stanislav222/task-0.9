package com.example.demo.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@RequiredArgsConstructor
public class KafkaTopicsConfig {
    private final KafkaPropertiesConfig config;
    @Bean
    public NewTopic topicFirst() {
        return TopicBuilder.name(config.getTopicsName())
                .partitions(config.getTopicsPartitions())
                .replicas(config.getTopicsReplicas())
                .build();
    }
}
