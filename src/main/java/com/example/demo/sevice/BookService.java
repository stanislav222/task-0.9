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

    public List<BookDto> readAll() {
        List<Book> bookList = bookDaoWithJdbcTemplate.readAll();
        return bookList.stream().map(modelMapper::bookModelConvertToBookDTO)
                .collect(Collectors.toList());
    }

    public boolean update(BookDto bookDto, int id) {
        return bookDaoWithJdbcTemplate.update(modelMapper.bookDTOConvertToBookModel(bookDto), id);
    }

    public boolean delete(int id) {
        return bookDaoWithJdbcTemplate.delete(id);
    }

    public List<BookDto> readFromOpenLibrary(String author) {
        return openLibraryExchangeClient.getBookFromOpenLibraryByAuthor(author).stream()
                .map(modelMapper::openLibraryDtoConvertToBookDTO).collect(Collectors.toList());
    }

    public List<BookDto> readBookByAuthorFromDbAndOL(String author) {
        List<Book> bookList = bookDaoWithJdbcTemplate.getBookByAuthor(author);
        List<BookDto> bookDtos = readFromOpenLibrary(author);
        return bookList.stream().map(modelMapper::bookModelConvertToBookDTO)
                .collect(Collectors.toCollection(() -> bookDtos));
    }

    public BigDecimal getPriceByTitle(String title) throws BookException {
        Optional<Book> priceByTitle = Optional.ofNullable(bookDaoWithJdbcTemplate.getPriceByTitle(title));
        return priceByTitle.map(Book::getCost)
                .orElseThrow(() ->new BookException(String.format("Price not found by title %s", title)));
    }

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
                "  \"other currency at the AlfaBank exchange rate\" : %s \n" +
                "}", title, priceByTitle.toString() , resultString);
    }

}
