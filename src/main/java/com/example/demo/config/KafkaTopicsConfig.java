package com.example.demo.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {
    @Bean
    public NewTopic topicFirst() {
        return TopicBuilder.name("audit")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
