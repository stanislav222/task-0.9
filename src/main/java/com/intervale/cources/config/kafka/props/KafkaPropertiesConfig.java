package com.intervale.cources.config.kafka.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaPropertiesConfig {
    @NotBlank
    private String bootstrapServers;
    @NotBlank
    private String topicsName;
    @NotBlank
    private String topicsGroupId;
    @NotNull
    private int topicsPartitions;
    @NotNull
    private int topicsReplicas;
}
