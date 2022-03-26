package com.intervale.cources.config;

import com.intervale.cources.interceptor.SendingToKafkaInterceptor;
import com.intervale.cources.util.EnumConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final EnumConverter enumConverter;
    private final SendingToKafkaInterceptor kafkaInterceptor;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(enumConverter);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(kafkaInterceptor).addPathPatterns("/api/v1/book/price/**");
    }


}
