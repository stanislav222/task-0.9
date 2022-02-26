package com.example.demo.external.alfabank.service;

import com.example.demo.dto.NationalRateDto;
import com.example.demo.dto.NationalRateListResponseDto;
import com.example.demo.external.alfabank.model.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AlfaBankExchangeClient {

    @Qualifier("alfaBankRestTemplate")
    private final RestTemplate restTemplate;

    public List<NationalRateDto> getTheCurrentCurrencySaleRate(List<Currency> currencyList) {
        List<Integer> collect = currencyList.stream().map(Currency::getCurrencyCode).collect(Collectors.toList());
        String codeCurrencies = collect.stream().map(String::valueOf).collect(Collectors.joining((",")));
        String url = "/partner/1.0.1/public/nationalRates?currencyCode="+ codeCurrencies;
        NationalRateListResponseDto listResponseDto = restTemplate.getForObject(url, NationalRateListResponseDto.class);
        assert listResponseDto != null;
        return listResponseDto.getRates();
    }
}
