package com.intervale.cources.external.openlibrary.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "open-library.settings")
public class OpenLibraryConfigProperties {
    @NotBlank
    private String baseUrl;
    @NotBlank
    private String limitRecord;
}
