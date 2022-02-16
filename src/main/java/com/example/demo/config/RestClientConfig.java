package com.example.demo.config;


import com.example.demo.exception.RestTemplateResponseErrorHandler;
import com.example.demo.interceptor.LoggingForRequestsToExternalResources;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.function.Supplier;


@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private final OpenLibraryConfigProperties openLibraryConfigProperties;
    private final RestTemplateResponseErrorHandler restTemplateResponseErrorHandler;
    private final LoggingForRequestsToExternalResources forRequestsToExternalResources;

    @Bean
    public RestTemplate restTemplate() {

        return new RestTemplateBuilder()
                .additionalCustomizers((restTemplate) -> restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(
                        new SimpleClientHttpRequestFactory())))
                .errorHandler(restTemplateResponseErrorHandler)
                .interceptors(forRequestsToExternalResources)
                .build();
             /*
                .setReadTimeout(Duration.between(OffsetDateTime.now(), OffsetDateTime.now().plusSeconds(15)))
                .setConnectTimeout(Duration.between(OffsetDateTime.now(), OffsetDateTime.now().plusSeconds(15)))*/
    }
}
