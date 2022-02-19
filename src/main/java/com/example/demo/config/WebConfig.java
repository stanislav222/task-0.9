package com.example.demo.config;

import com.example.demo.interceptor.SendingToKafka;
import com.example.demo.util.EnumConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final EnumConverter enumConverter;
    private final SendingToKafka sendingToKafka;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(enumConverter);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(sendingToKafka).addPathPatterns("/api/book/price/**");
    }


}
