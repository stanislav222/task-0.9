package com.example.demo.sevice;

import com.example.demo.dao.BookDaoWithJdbcTemplate;
import com.example.demo.dto.NationalRateDto;
import com.example.demo.exception.BookException;
import com.example.demo.external.alfabank.AlfaBankExchangeClient;
import com.example.demo.external.alfabank.model.Currency;
import com.example.demo.external.openlibrary.OpenLibraryExchangeClient;
import com.example.demo.model.Book;
import com.example.demo.model.dto.BookDto;
import com.example.demo.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BookService {

    private final BookDaoWithJdbcTemplate bookDaoWithJdbcTemplate;
    private final OpenLibraryExchangeClient openLibraryExchangeClient;
    private final AlfaBankExchangeClient alfaBankExchangeClient;
    private final ModelMapper modelMapper;

    public void create(BookDto bookDto) {
        bookDaoWithJdbcTemplate.createBook(modelMapper.bookDTOConvertToBookModel(bookDto));
    }

    /**
     * readAll : list книг из БД
     * @return Успешное выполнение запроса возвращает - List BookDto
     *          Ошибка выполнения запроса - пустой List BookDto
     */
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
    public boolean update(BookDto bookDto, int id) {
        return bookDaoWithJdbcTemplate.update(modelMapper.bookDTOConvertToBookModel(bookDto), id);
    }

    /**
     * delete : удаление книги в БД
     * @param id Id книги
     * @return Успешное выполнение запроса возвращает - boolean = true
     *         Ошибка выполнения запроса - boolean = false
     */
    public boolean delete(int id) {
        return bookDaoWithJdbcTemplate.delete(id);
    }

    /**
     * readFromOpenLibrary : list книг из OpenLibrary
     * @param author Имя автора
     * @return Успешное выполнение запроса возвращает - List BookDto
     *          Ошибка выполнения запроса - пустой List BookDto
     */
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
    public BigDecimal getPriceByTitle(String title) throws BookException {
        Optional<Book> priceByTitle = Optional.ofNullable(bookDaoWithJdbcTemplate.getPriceByTitle(title));
        return priceByTitle.map(Book::getCost)
                .orElseThrow(() ->new BookException(String.format("Price not found by title %s", title)));
    }

    /**
     * getPriceByTitle : цена книги по названию книги из БД, также конвертация из BLR в другую валюту по нац. банку
     * @param title Название книги
     * @param currencies Список валют Enum Currency (USD, EUR, RUB)
     * @return Успешное выполнение запроса возвращает - цену в формате строки {
     *                   "title": title,
     *                   "BLR": price,
     *                   "national bank exchange rate" : price in another currency
     *                 }
     *         Ошибка выполнения запроса - цена по названиб не найдена кидает BookException
     */
    public String getPriceByTitleWithCostInDifferentCurrencies(String title, List<Currency> currencies) throws BookException {
        BigDecimal priceByTitle = getPriceByTitle(title);
        List<NationalRateDto> saleRate = alfaBankExchangeClient.getTheCurrentCurrencySaleRate(currencies);
        Map<String, BigDecimal> resultRate = saleRate.stream()
                .collect(Collectors.toMap(NationalRateDto::getIso, i -> priceByTitle
                        .divide(i.getRate()
                                .divide(BigDecimal.valueOf(i.getQuantity()), 4, RoundingMode.HALF_UP), 4, RoundingMode.HALF_UP)));
        String resultString = resultRate.entrySet().stream().map(i -> i.getKey() + " : " + i.getValue())
                .collect(Collectors.joining(", "));
        return String.format("{\n" +
                "  \"title\": %s,\n" +
                "  \"BLR\": %s,\n" +
                "  \"national bank exchange rate\" : %s \n" +
                "}", title, priceByTitle.toString() , resultString);
    }

}
