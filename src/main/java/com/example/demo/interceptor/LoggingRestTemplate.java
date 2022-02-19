package com.example.demo.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
@Component
public class LoggingRestTemplate implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        loggingRequest(httpRequest, body);
        ClientHttpResponse response = execution.execute(httpRequest, body);
        loggingResponse(response);
        return response;
    }

    private void loggingRequest(HttpRequest request, byte[] body) throws IOException {
        log.info("=>".repeat(12) + " Request to external resource start");
        log.info("URI         : {}", request.getURI());
        log.info("Method      : {}", request.getMethod());
        log.info("Headers     : {}", request.getHeaders());
        log.info("Request body: {}", new String(body, StandardCharsets.UTF_8));
        log.info("..".repeat(12)  + " Request to external resource end");
    }

    private void loggingResponse(ClientHttpResponse response) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8));
        String collect = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
        log.info("=>".repeat(12)  + " Response to external resource start");
        log.info("Status code  : {}", response.getStatusCode());
        log.info("Status text  : {}", response.getStatusText());
        log.info("Headers      : {}", response.getHeaders());
        log.info("Response body: {}", collect);
        log.info("..".repeat(12)  + " Response to external resource end");
    }

}