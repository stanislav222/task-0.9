package com.intervale.cources.util;

import com.intervale.cources.exception.BookException;
import com.intervale.cources.external.alfabank.model.Currency;
import lombok.SneakyThrows;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class EnumConverter implements Converter<String, Currency> {

    @SneakyThrows
    @Override
    public Currency convert(String source) {
        try {
            return Currency.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
             throw new BookException("There are three types of currencies available: RUB, EUR, USD. Request example - /{books title}?nameCurrency=RUB, USD");
        }
    }
}
