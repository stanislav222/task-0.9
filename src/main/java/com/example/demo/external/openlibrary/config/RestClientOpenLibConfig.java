package com.example.demo.external.openlibrary.config;

import com.example.demo.exception.RestTemplateResponseErrorHandler;
import com.example.demo.interceptor.LoggingRestTemplateInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class RestClientOpenLibConfig {

    private final RestTemplateResponseErrorHandler restTemplateResponseErrorHandler;
    private final LoggingRestTemplateInterceptor forRequestsToExternalResources;
    private final OpenLibraryConfigProperties configProperties;

    @Bean(name = "openLibRestTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .additionalCustomizers((restTemplate) ->
                        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(
                                new SimpleClientHttpRequestFactory())))
                .rootUri(configProperties.getBaseUrl())
                .errorHandler(restTemplateResponseErrorHandler)
                .interceptors(forRequestsToExternalResources)
                .build();
    }
}
