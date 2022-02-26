package com.example.demo.external.openlibrary.service;

import com.example.demo.exception.RestTemplateResponseErrorHandler;
import com.example.demo.external.openlibrary.config.OpenLibraryConfigProperties;
import com.example.demo.external.openlibrary.config.RestClientOpenLibConfig;
import com.example.demo.external.openlibrary.dto.AuthorFromOpenLibDto;
import com.example.demo.external.openlibrary.dto.BookFromOpenLibraryDto;
import com.example.demo.interceptor.LoggingRestTemplateInterceptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest
@ContextConfiguration(classes = {
        RestClientOpenLibConfig.class ,
        RestTemplateResponseErrorHandler.class,
        LoggingRestTemplateInterceptor.class,
        OpenLibraryExchangeClient.class,
        OpenLibraryConfigProperties.class
    })
class OpenLibraryExchangeClientTest {

    @Value("classpath:author.json")
    Resource author;

    @Value("classpath:booksFromOL.json")
    Resource books;

    @Autowired
    @Qualifier("openLibRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    @Autowired
    private OpenLibraryExchangeClient openLibraryExchangeClient;

    private String testLink = "https://openlibrary.org/search/authors.json?q=Rowling";
    private String testLinkTwo = "https://openlibrary.org/authors/OL23919A/works.json?limit=10";

    @BeforeEach
    public void setUp() {
        mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate)
                .ignoreExpectOrder(true)
                .bufferContent() //// enable repeated reads of response body
                .build();
    }

    @Test
    public void getAuthorKeyFromOpenLibrary(){
        this.mockRestServiceServer.expect(ExpectedCount.once(), requestTo(testLink))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(author, MediaType.APPLICATION_JSON));
        AuthorFromOpenLibDto authorFromOpenLibDto = openLibraryExchangeClient.getAuthorKeyFromOpenLibrary("Rowling");
        Assertions.assertNotNull(authorFromOpenLibDto);
        Assertions.assertEquals("OL23919A", authorFromOpenLibDto.getKey());
        mockRestServiceServer.verify();
    }

    @Test
    public void getAuthorKeyFromOpenLibraryWithException(){
        this.mockRestServiceServer.expect(ExpectedCount.once(), requestTo(testLink))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));
        Assertions.assertThrows(NullPointerException.class, () ->{
            openLibraryExchangeClient.getAuthorKeyFromOpenLibrary("Rowling");
        });
    }

    @Test
    void getBookFromOpenLibraryByAuthor() {
        this.mockRestServiceServer.expect(ExpectedCount.once(), requestTo(testLink))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(author, MediaType.APPLICATION_JSON));
        this.mockRestServiceServer.expect(ExpectedCount.once(), requestTo(testLinkTwo))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(books, MediaType.APPLICATION_JSON));
        List<BookFromOpenLibraryDto> rowling = openLibraryExchangeClient.getBookFromOpenLibraryByAuthor("Rowling");
        Assertions.assertNotNull(rowling);
        Assertions.assertEquals("HARRY POTTER & DER STEIN - ROW", rowling.get(0).getTitle());
        mockRestServiceServer.verify();
    }


    @Test
    void getBookFromOpenLibraryByAuthorWithException() {
        this.mockRestServiceServer.expect(ExpectedCount.once(), requestTo(testLink))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(author, MediaType.APPLICATION_JSON));
        this.mockRestServiceServer.expect(ExpectedCount.once(), requestTo(testLinkTwo))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));
        Assertions.assertThrows(AssertionError.class, () ->{
            openLibraryExchangeClient.getBookFromOpenLibraryByAuthor("Rowling");
        });
    }

}