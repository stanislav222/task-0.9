package com.example.demo.config;


import com.example.demo.exception.RestTemplateResponseErrorHandler;
import com.example.demo.interceptor.LoggingRestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


@Configuration
@RequiredArgsConstructor
public class RestClientConfig {
    private final RestTemplateResponseErrorHandler restTemplateResponseErrorHandler;
    private final LoggingRestTemplate forRequestsToExternalResources;

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
