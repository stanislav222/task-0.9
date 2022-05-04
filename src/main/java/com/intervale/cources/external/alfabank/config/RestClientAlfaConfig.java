package com.intervale.cources.external.alfabank.config;

import com.intervale.cources.exception.RestTemplateResponseErrorHandler;
import com.intervale.cources.interceptor.LoggingRestTemplateInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


@Configuration
@RequiredArgsConstructor
public class RestClientAlfaConfig {

    private final RestTemplateResponseErrorHandler restTemplateResponseErrorHandler;
    private final LoggingRestTemplateInterceptor forRequestsToExternalResources;

    @Value("${alfa-bank.setting.base-url}")
    private String alfaBaseUrl;

    @Bean(name = "alfaBankRestTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .additionalCustomizers((restTemplate) ->
                        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(
                                new SimpleClientHttpRequestFactory())))
                .rootUri(alfaBaseUrl)
                .errorHandler(restTemplateResponseErrorHandler)
                .interceptors(forRequestsToExternalResources)
                .build();
    }
}
