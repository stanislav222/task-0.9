package com.example.demo.external.alfabank.service;

import com.example.demo.dto.NationalRateDto;
import com.example.demo.dto.NationalRateListResponseDto;
import com.example.demo.external.alfabank.model.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AlfaBankExchangeWithWebClient {

    @Qualifier("webClientAlfaBank")
    private final WebClient webClient;

    public List<NationalRateDto> getTheCurrentCurrencySaleRate(List<Currency> currencyList) {
        List<Integer> collect = currencyList.stream().map(Currency::getCurrencyCode).collect(Collectors.toList());
        String codeCurrencies = collect.stream().map(String::valueOf).collect(Collectors.joining((",")));
        String url = "/partner/1.0.1/public/nationalRates?currencyCode="+ codeCurrencies;
        NationalRateListResponseDto responseDto = webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(NationalRateListResponseDto.class)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Something went wrong"));
        return responseDto.getRates();
    }
}
