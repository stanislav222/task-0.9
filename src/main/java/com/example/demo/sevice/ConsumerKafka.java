package com.example.demo.sevice;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "enable", name = "kafkaListener", matchIfMissing = true)
public class ConsumerKafka {
    @KafkaListener(topics = {"audit"}, groupId = "book-group")
    public void consume(ConsumerRecord<String, String> consumerRecord) {
        log.info("Key: {} - Info: {}",
                consumerRecord.key().replace("�", ""),
                consumerRecord.value().replace("�", ""));
    }
}
