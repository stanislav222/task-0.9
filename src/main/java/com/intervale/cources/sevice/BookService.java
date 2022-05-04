package com.intervale.cources.sevice;

import com.intervale.cources.exception.BookException;
import com.intervale.cources.external.alfabank.model.Currency;
import com.intervale.cources.model.dto.BookDto;
import com.intervale.cources.model.dto.SimpleBankCurrencyExchangeRateDto;

import java.math.BigDecimal;
import java.util.List;

public interface BookService {
    boolean create(BookDto bookDto);

    List<BookDto> readAll();

    boolean update(BookDto bookDto, int id);

    boolean delete(int id);

    List<BookDto> readFromOpenLibrary(String author);

    List<BookDto> readBookByAuthorFromDbAndOL(String author);

    BigDecimal getPriceByTitle(String title) throws BookException;

    SimpleBankCurrencyExchangeRateDto getPriceByTitleWithCostInDifferentCurrencies
            (String title, List<Currency> currencies) throws BookException;

    SimpleBankCurrencyExchangeRateDto getPriceByTitleWithCostInDifferentCurrenciesForPeriodOfTime
            (String title, List<Currency> currencies) throws BookException;
}
