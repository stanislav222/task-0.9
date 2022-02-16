package com.example.demo.external;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenLibraryExchangeClientTest2 {

    private RestTemplate restTemplate = new RestTemplateBuilder()
            .defaultMessageConverters()
            .setBufferRequestBody(true)
            .setReadTimeout(Duration.between(OffsetDateTime.now(), OffsetDateTime.now().plusSeconds(15)))
            .setConnectTimeout(Duration.between(OffsetDateTime.now(), OffsetDateTime.now().plusSeconds(15)))
            .build();

    @SneakyThrows
    @Test
    public void getAuthorKeyFromOpenLibrary(){
        //RestTemplate restTemplate = new RestTemplate();
        String url = "https://openlibrary.org/search/authors.json?q=rowling";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        System.out.println(response.getBody());
        assertNotNull(response.getBody());
    }
}