package com.example.demo.external.openlibrary.service;

import com.example.demo.external.WebClientFilter;
import com.example.demo.external.WebClientFilterAdvanced;
import com.example.demo.external.openlibrary.config.OpenLibraryConfigProperties;
import com.example.demo.external.openlibrary.dto.AuthorFromOpenLibDto;
import com.example.demo.external.openlibrary.dto.BookFromOpenLibraryDto;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

class OpenLibraryExchangeClientWithWebClientTest {

    private MockWebServer mockWebServer;
    private OpenLibraryExchangeClientWithWebClient exchangeClientWithWebClient;

    @BeforeEach
    void setupMockWebServer() throws IOException {
        mockWebServer = new MockWebServer();
        OpenLibraryConfigProperties properties = new OpenLibraryConfigProperties();
        properties.setBaseUrl(mockWebServer.url("/").url().toString());
        properties.setLimitRecord("10");
        exchangeClientWithWebClient = new OpenLibraryExchangeClientWithWebClient(WebClient
                .builder()
                .baseUrl(properties.getBaseUrl())
                .filter(new WebClientFilter().errorResponseFilter())
                .filter(new WebClientFilterAdvanced())
                .build(), properties);
    }

    @Test
    void getBookFromOpenLibraryByAuthor() {
        MockResponse mockResponseOne = new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(getJson("author.json"));
        mockWebServer.enqueue(mockResponseOne);
        MockResponse mockResponseTwo = new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(getJson("booksFromOL.json"));
        mockWebServer.enqueue(mockResponseTwo);
        List<BookFromOpenLibraryDto> rowling = exchangeClientWithWebClient.getBookFromOpenLibraryByAuthor("Rowling");
        Assertions.assertNotNull(rowling);
        Assertions.assertEquals("HARRY POTTER & DER STEIN - ROW", rowling.get(0).getTitle());
    }

    @Test
    void getAuthorKeyFromOpenLibrary() {
        MockResponse mockResponse = new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(getJson("author.json"));
        mockWebServer.enqueue(mockResponse);
        AuthorFromOpenLibDto keyFromOpenLibrary = exchangeClientWithWebClient.getAuthorKeyFromOpenLibrary("Rowling");
        Assertions.assertNotNull(keyFromOpenLibrary);
        Assertions.assertEquals("OL23919A", keyFromOpenLibrary.getKey());
    }

    @Test
    void getAuthorKeyFromOpenLibraryWithException() {
        MockResponse mockResponse = new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(getJson("author.json"))
                .setResponseCode(501);
        mockWebServer.enqueue(mockResponse);
        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> {
            exchangeClientWithWebClient.getAuthorKeyFromOpenLibrary("Rowling");
        });
        Assertions.assertEquals("Server error", runtimeException.getMessage());
    }

    private String getJson(String path) {
        try {
            InputStream jsonStream = this.getClass().getClassLoader().getResourceAsStream(path);
            assert jsonStream != null;
            return new String(jsonStream.readAllBytes());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}