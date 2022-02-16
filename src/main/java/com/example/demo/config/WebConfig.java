package com.example.demo.config;

import com.example.demo.util.EnumConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final EnumConverter enumConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(enumConverter);
    }
}
