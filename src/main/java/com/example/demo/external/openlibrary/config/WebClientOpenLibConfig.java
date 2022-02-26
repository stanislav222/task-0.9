package com.example.demo.external.openlibrary.config;

import com.example.demo.external.WebClientFilter;
import com.example.demo.external.WebClientFilterAdvanced;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class WebClientOpenLibConfig {

    private final OpenLibraryConfigProperties configProperties;
    private final WebClientFilter webClientFilter;
    private final WebClientFilterAdvanced webClientFilterAdvanced;

    @Bean
    public WebClient webClientOpenLib() {
        HttpClient httpClient = HttpClient.create()
                //время ожидания соединения
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000)
                //время ожидания ответа
                .responseTimeout(Duration.ofSeconds(2))
                //таймауты чтения и записи
                .doOnConnected(connection ->
                        connection
                                .addHandlerLast(new ReadTimeoutHandler(2))
                                .addHandlerLast(new WriteTimeoutHandler(2)));
        return WebClient.builder()
                .baseUrl(configProperties.getBaseUrl())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filter(webClientFilter.errorResponseFilter())
                .filter(webClientFilterAdvanced)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
