package com.example.demo.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.SuccessCallback;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Clock;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendingToKafka implements HandlerInterceptor {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<String, List<String>> headersMap = Collections.list(request.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        header -> Collections.list(request.getHeaders(header))
                ));
        Clock clock = Clock.systemUTC();
        kafkaTemplate.send("audit", request.getRemoteAddr(),
                String.format("Time UTC: %s, Headers: %s", clock.instant(), headersMap.toString()))
                .addCallback(result -> {
                    if (result != null) {
                        RecordMetadata metadata = result.getRecordMetadata();
                        log.info("produce to {}, {}, {}",
                                metadata.topic(),
                                metadata.partition(),
                                metadata.offset());
                    }
                }, ex -> {
                });
        kafkaTemplate.flush();
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (ex != null){
            log.error("Something went wrong");
        }
    }
}
