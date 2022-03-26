package com.intervale.cources.external.alfabank.service;

import com.intervale.cources.dto.NationalRateDto;
import com.intervale.cources.dto.NationalRateListResponseDto;
import com.intervale.cources.exception.NationalBankException;
import com.intervale.cources.external.alfabank.model.Currency;
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

    private final static String NT_ROUT = "/partner/1.0.1/public/nationalRates?currencyCode=";

    public List<NationalRateDto> getTheCurrentCurrencySaleRate(List<Currency> currencyList) {
        List<Integer> collect = currencyList.stream().map(Currency::getCurrencyCode).collect(Collectors.toList());
        String codeCurrencies = collect.stream().map(String::valueOf).collect(Collectors.joining((",")));
        String url = NT_ROUT + codeCurrencies;
        NationalRateListResponseDto listResponseDto = restTemplate.getForObject(url, NationalRateListResponseDto.class);
        if(listResponseDto == null){
            throw new NationalBankException("The answer received from National Bank is not correct");
        }
        return listResponseDto.getRates();
    }
}
