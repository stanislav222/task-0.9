package com.example.demo.external.openlibrary;

import com.example.demo.config.RestClientConfig;
import com.example.demo.exception.RestTemplateResponseErrorHandler;
import com.example.demo.interceptor.LoggingRestTemplate;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@RestClientTest
@ContextConfiguration(classes = {
        RestClientConfig.class ,
        RestTemplateResponseErrorHandler.class,
        LoggingRestTemplate.class})
class OpenLibraryExchangeClientTest2 {

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void getAuthorKeyFromOpenLibrary(){
        String url = "https://openlibrary.org/search/authors.json?q=rowling";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        System.out.println(response.getBody());
        assertNotNull(response.getBody());
    }
}