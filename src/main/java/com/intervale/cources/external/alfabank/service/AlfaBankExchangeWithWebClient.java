package com.intervale.cources.external.alfabank.service;

import com.intervale.cources.dto.NationalRateDto;
import com.intervale.cources.dto.NationalRateListResponseDto;
import com.intervale.cources.exception.NationalBankException;
import com.intervale.cources.external.alfabank.model.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AlfaBankExchangeWithWebClient {

    @Qualifier("webClientAlfaBank")
    private final WebClient webClient;

    private final static String NT_ROUT = "/partner/1.0.1/public/nationalRates?currencyCode=";
    private final static String DATE_PART = "&date={date}";

    public List<NationalRateDto> getTheCurrentCurrencySaleRate(List<Currency> currencyList) {
        List<Integer> collect = currencyList.stream().map(Currency::getCurrencyCode).collect(Collectors.toList());
        String codeCurrencies = collect.stream().map(String::valueOf).collect(Collectors.joining((",")));
        String url = NT_ROUT + codeCurrencies;
        NationalRateListResponseDto responseDto = webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(NationalRateListResponseDto.class)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Something went wrong"));
        return responseDto.getRates();
    }

    public Map<String, List<NationalRateDto>> getTheCurrentCurrencySaleRateWithRangeDate(List<Currency> currencyList) {
        List<String> dateRange = getDatesBetween();
        Flux<NationalRateListResponseDto> nationalRateListResponseDtoFlux = Flux.fromIterable(dateRange)
                .flatMap(date ->
                        getTheCurrentCurrencySaleRateWithDate(currencyList, date));
        Set<NationalRateListResponseDto> block = nationalRateListResponseDtoFlux.collect(Collectors.toSet()).block();
        if(block == null){
            throw new NationalBankException("The answer received from National Bank is not correct");
        }
        return block.stream()
                .flatMap(rate -> rate.getRates().stream())
                .collect(Collectors.groupingBy(NationalRateDto::getDate));
    }

    public Mono<NationalRateListResponseDto> getTheCurrentCurrencySaleRateWithDate(List<Currency> currencyList, String date) {
        String codeCurrencies = currencyList.stream()
                .map(Currency::getCurrencyCode).map(String::valueOf).collect(Collectors.joining((",")));
        String url = NT_ROUT + codeCurrencies;
        return webClient
                .get()
                .uri(url + DATE_PART, date)
                .retrieve()
                .bodyToMono(NationalRateListResponseDto.class);

    }

    private List<String> getDatesBetween() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate startDate = LocalDate.now();
        System.out.println(startDate);
        LocalDate endDate = startDate.minusDays(10);
        return endDate.datesUntil(startDate)
                .map(dtf::format)
                .collect(Collectors.toList());
    }
}
