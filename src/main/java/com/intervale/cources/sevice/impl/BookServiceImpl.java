package com.intervale.cources.sevice.impl;

import com.intervale.cources.dao.BookDao;
import com.intervale.cources.dto.NationalRateDto;
import com.intervale.cources.exception.BookException;
import com.intervale.cources.external.alfabank.model.Currency;
import com.intervale.cources.external.alfabank.service.AlfaBankExchangeWithWebClient;
import com.intervale.cources.external.openlibrary.service.OpenLibraryExchangeClientWithWebClient;
import com.intervale.cources.model.Book;
import com.intervale.cources.model.dto.BookDto;
import com.intervale.cources.model.dto.SimpleBankCurrencyExchangeRateDto;
import com.intervale.cources.sevice.BookService;
import com.intervale.cources.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    @Qualifier("bookDaoWithJdbcTemplate")
    private final BookDao bookDaoWithJdbcTemplate;
    private final OpenLibraryExchangeClientWithWebClient openLibraryExchangeClient;
    private final AlfaBankExchangeWithWebClient alfaBankExchangeClient;
    private final ModelMapper modelMapper;

    @Override
    public boolean create(BookDto bookDto) {
        return bookDaoWithJdbcTemplate.createBook(modelMapper.bookDTOConvertToBookModel(bookDto));
    }

    /**
     * readAll : list книг из БД
     * @return Успешное выполнение запроса возвращает - List BookDto
     *          Ошибка выполнения запроса - пустой List BookDto
     */
    @Override
    public List<BookDto> readAll() {
        List<Book> bookList = bookDaoWithJdbcTemplate.readAll();
        return bookList.stream().map(modelMapper::bookModelConvertToBookDTO)
                .collect(Collectors.toList());
    }

    /**
     * update : обновление книги в БД
     * @param bookDto Модель книги
     * @param id Id книги
     * @return Успешное выполнение запроса возвращает - boolean = true
     *         Ошибка выполнения запроса - boolean = false
     */
    @Override
    public boolean update(BookDto bookDto, int id) {
        return bookDaoWithJdbcTemplate.update(modelMapper.bookDTOConvertToBookModel(bookDto), id);
    }

    /**
     * delete : удаление книги в БД
     * @param id Id книги
     * @return Успешное выполнение запроса возвращает - boolean = true
     *         Ошибка выполнения запроса - boolean = false
     */
    @Override
    public boolean delete(int id) {
        return bookDaoWithJdbcTemplate.delete(id);
    }

    /**
     * readFromOpenLibrary : list книг из OpenLibrary
     * @param author Имя автора
     * @return Успешное выполнение запроса возвращает - List BookDto
     *          Ошибка выполнения запроса - пустой List BookDto
     */
    @Override
    public List<BookDto> readFromOpenLibrary(String author) {
        return openLibraryExchangeClient.getBookFromOpenLibraryByAuthor(author).stream()
                .map(modelMapper::openLibraryDtoConvertToBookDTO).collect(Collectors.toList());
    }

    /**
     * readBookByAuthorFromDbAndOL : list книг из OpenLibrary и БД вместе
     * @param author Имя автора
     * @return Успешное выполнение запроса возвращает - List BookDto
     *         Ошибка выполнения запроса - пустой List BookDto
     */
    @Override
    public List<BookDto> readBookByAuthorFromDbAndOL(String author) {
        List<Book> bookList = bookDaoWithJdbcTemplate.getBookByAuthor(author);
        List<BookDto> bookDtos = readFromOpenLibrary(author);
        return bookList.stream().map(modelMapper::bookModelConvertToBookDTO)
                .collect(Collectors.toCollection(() -> bookDtos));
    }

    /**
     * getPriceByTitle : цена книги по названию книги из БД
     * @param title Название книги
     * @return Успешное выполнение запроса возвращает - цену в формате BigDecimal
     *         Ошибка выполнения запроса - цена по названию книги не найдена, кидает BookException
     */
    @Override
    public BigDecimal getPriceByTitle(String title) throws BookException {
        Optional<Book> priceByTitle = Optional.ofNullable(bookDaoWithJdbcTemplate.getPriceByTitle(title));
        return priceByTitle.map(Book::getCost)
                .orElseThrow(() ->new BookException(String.format("Price not found by title %s", title)));
    }

    /**
     * getPriceByTitle : цена книги по названию книги из БД, также конвертация из BLR в другую валюту по нац. банку
     * @param title Название книги
     * @param currencies Список валют Enum Currency (USD, EUR, RUB)
     * @return Успешное выполнение запроса возвращает - SimpleBankCurrencyExchangeRateDto
     *         Ошибка выполнения запроса - цена по названиб не найдена кидает BookException
     */
    @Override
    public SimpleBankCurrencyExchangeRateDto getPriceByTitleWithCostInDifferentCurrencies
    (String title, List<Currency> currencies) throws BookException {
        BigDecimal priceByTitle = getPriceByTitle(title);
        List<NationalRateDto> saleRate = alfaBankExchangeClient.getTheCurrentCurrencySaleRate(currencies);
        Map<String, BigDecimal> resultRate = getStringBigDecimalMap(priceByTitle, saleRate);
        String resultString = resultRate.entrySet().stream().map(i -> i.getKey() + " : " + i.getValue())
                .collect(Collectors.joining(", "));
        return SimpleBankCurrencyExchangeRateDto.builder()
                .title(title)
                .price(priceByTitle)
                .nationalBankExchangeRate(resultString)
                .build();
    }

    @Override
    public SimpleBankCurrencyExchangeRateDto getPriceByTitleWithCostInDifferentCurrenciesForPeriodOfTime
            (String title, List<Currency> currencies) throws BookException {
        BigDecimal priceByTitle = getPriceByTitle(title);
        Map<String, List<NationalRateDto>> rateWithRangeDate = alfaBankExchangeClient
                .getTheCurrentCurrencySaleRateWithRangeDate(currencies);
        Map<String, Map<String, BigDecimal>> collect = rateWithRangeDate.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> getStringBigDecimalMap(priceByTitle, e.getValue())));
        Map<String, Map<String, BigDecimal>> sortedResult = collect.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return SimpleBankCurrencyExchangeRateDto.builder()
                .title(title)
                .price(priceByTitle)
                .nationalBankExchangeRate(sortedResult)
                .build();
    }


    private Map<String, BigDecimal> getStringBigDecimalMap(BigDecimal priceByTitle, List<NationalRateDto> saleRate) {
        return saleRate.stream()
                    .collect(Collectors.toMap(NationalRateDto::getIso, i -> priceByTitle
                            .divide(i.getRate()
                                    .divide(BigDecimal.valueOf(i.getQuantity()), 4, RoundingMode.HALF_UP), 4, RoundingMode.HALF_UP)));
    }
}
