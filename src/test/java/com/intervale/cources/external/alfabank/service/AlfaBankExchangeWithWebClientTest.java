package com.intervale.cources.external.alfabank.service;

import com.intervale.cources.dto.NationalRateDto;
import com.intervale.cources.external.WebClientFilter;
import com.intervale.cources.external.WebClientFilterAdvanced;
import com.intervale.cources.external.alfabank.model.Currency;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class AlfaBankExchangeWithWebClientTest {

    private MockWebServer mockWebServer;
    private AlfaBankExchangeWithWebClient exchangeRateClient;

    @BeforeEach
    void setupMockWebServer() throws IOException {
        mockWebServer = new MockWebServer();
        exchangeRateClient = new AlfaBankExchangeWithWebClient(WebClient.builder()
                .baseUrl(mockWebServer.url("/").url().toString())
                .filter(new WebClientFilter().errorResponseFilter())
                .filter(new WebClientFilterAdvanced())
                .build());
    }

    @Test
    void getTheCurrentCurrencySaleRate() {
        MockResponse mockResponse = new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(getJson("rates.json"));
        mockWebServer.enqueue(mockResponse);
        List<NationalRateDto> theCurrentCurrencySaleRate = exchangeRateClient.getTheCurrentCurrencySaleRate(List.of(Currency.EUR));
        Assertions.assertNotNull(theCurrentCurrencySaleRate);
        Assertions.assertEquals("EUR", theCurrentCurrencySaleRate.get(0).getIso());
    }

    @Test
    void getTheCurrentCurrencyWithException() {
        MockResponse mockResponseWithException = new MockResponse()
                .setBody(getJson("rates.json"))
                .setResponseCode(400);
        mockWebServer.enqueue(mockResponseWithException);
        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> {
            exchangeRateClient.getTheCurrentCurrencySaleRate(List.of(Currency.EUR));
        });
        Assertions.assertEquals("API not found", runtimeException.getMessage());
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

    @Test
    void getTheCurrentCurrencySaleRateWithRangeDate() {
        AlfaBankExchangeWithWebClient alfaBankExchangeWithWebClient = new AlfaBankExchangeWithWebClient(WebClient.builder()
                .baseUrl(mockWebServer.url("https://developerhub.alfabank.by:8273").url().toString())
                .filter(new WebClientFilter().errorResponseFilter())
                .filter(new WebClientFilterAdvanced())
                .build());
        Map<String, List<NationalRateDto>> myObjs = alfaBankExchangeWithWebClient.getTheCurrentCurrencySaleRateWithRangeDate(List.of(Currency.EUR, Currency.RUB));
        System.out.println(myObjs);
    }

    @Test
    void getDatesBetweenUsingJava9() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate endDate = LocalDate.now();
        System.out.println(endDate);
        LocalDate startDate = endDate.minusDays(30);
        List<String> collect = startDate.datesUntil(endDate.plusDays(1))
                .map(dtf::format)
                .collect(Collectors.toList());
        System.out.println(collect);
    }
}