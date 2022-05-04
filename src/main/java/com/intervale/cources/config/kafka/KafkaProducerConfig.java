package com.intervale.cources.config.kafka;

import com.intervale.cources.config.kafka.props.KafkaPropertiesConfig;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final KafkaPropertiesConfig config;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(
                Map.of(
                        BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers(),
                        ACKS_CONFIG, "-1",
                        RETRIES_CONFIG, 1,
                        BATCH_SIZE_CONFIG, 2222,
                        LINGER_MS_CONFIG, 10,
                        BUFFER_MEMORY_CONFIG, 33_554_432,
                        MAX_BLOCK_MS_CONFIG, 1_000,
                        KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                        VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class
                ));
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
