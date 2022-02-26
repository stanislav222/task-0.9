package com.example.demo.external.alfabank.service;

import com.example.demo.dto.NationalRateDto;
import com.example.demo.exception.RestTemplateResponseErrorHandler;
import com.example.demo.external.alfabank.config.RestClientAlfaConfig;
import com.example.demo.external.alfabank.model.Currency;
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
        RestClientAlfaConfig.class ,
        RestTemplateResponseErrorHandler.class,
        LoggingRestTemplateInterceptor.class,
        AlfaBankExchangeClient.class
})
class AlfaBankExchangeClientTest {

    @Value("classpath:rates.json")
    Resource stateFile;

    @Autowired
    private AlfaBankExchangeClient alfaBankExchangeClient;

    @Autowired
    @Qualifier("alfaBankRestTemplate")
    RestTemplate restTemplate;

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    private String testLink = "https://developerhub.alfabank.by:8273/partner/1.0.1/public/nationalRates?currencyCode=978";

    @BeforeEach
    public void setUp() {
        mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate)
                .ignoreExpectOrder(true)
                .bufferContent() //// enable repeated reads of response body
                .build();
    }

    @Test
    void getTheCurrentCurrencySaleRate() {
        this.mockRestServiceServer.expect(ExpectedCount.once(), requestTo(testLink))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stateFile, MediaType.APPLICATION_JSON));
        List<NationalRateDto> theCurrentCurrencySaleRate = alfaBankExchangeClient.getTheCurrentCurrencySaleRate(List.of(Currency.EUR));
        Assertions.assertNotNull(theCurrentCurrencySaleRate);
        Assertions.assertEquals("EUR", theCurrentCurrencySaleRate.get(0).getIso());
        mockRestServiceServer.verify();
    }
    @Test
    void getTheCurrentCurrencySaleRateWithError404() {
        this.mockRestServiceServer.expect(ExpectedCount.once(), requestTo(testLink))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));
        Assertions.assertThrows(AssertionError.class, () ->{
            alfaBankExchangeClient.getTheCurrentCurrencySaleRate(List.of(Currency.EUR));
        });
    }
}