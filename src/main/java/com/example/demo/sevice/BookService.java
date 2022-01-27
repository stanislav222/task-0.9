package com.example.demo.sevice;

import com.example.demo.dao.BookDaoWithJdbcTemplate;
import com.example.demo.model.Book;
import com.example.demo.model.dto.BookDto;
import com.example.demo.model.dto.BookFromOpenLibraryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BookService {

    private BookDaoWithJdbcTemplate bookDaoWithJdbcTemplate;

    private OpenLibraryExchangeClient openLibraryExchangeClient;


    public void create(BookDto bookDto) {
        bookDaoWithJdbcTemplate.createBook(bookDTOConvertToBookModel(bookDto));
    }

    public List<BookDto> readAll() {
        List<Book> bookList = bookDaoWithJdbcTemplate.readAll();
        return bookList.stream().map(this::bookModelConvertToBookDTO)
                .collect(Collectors.toList());
    }

    public boolean update(BookDto bookDto, int id) {
        return bookDaoWithJdbcTemplate.update(bookDTOConvertToBookModel(bookDto), id);
    }

    public boolean delete(int id) {
        return bookDaoWithJdbcTemplate.delete(id);
    }

    public List<BookDto> readFromOpenLibrary(String author) {
        return openLibraryExchangeClient.getBookFromOpenLibraryByAuthor(author).stream()
                .map(this::openLibraryDtoConvertToBookDTO).collect(Collectors.toList());
    }

    public List<BookDto> readBookByAuthorFromDbAndOL(String author) {
        List<Book> bookList = bookDaoWithJdbcTemplate.getBookByAuthor(author);
        List<BookDto> bookDtos = readFromOpenLibrary(author);
        return bookList.stream().map(this::bookModelConvertToBookDTO)
                .collect(Collectors.toCollection(() -> bookDtos));
    }


    private BookDto openLibraryDtoConvertToBookDTO(@NotNull BookFromOpenLibraryDto input) {
        String info = "info missing in openLibrary";
        return new BookDto(
                info,
                input.getTitle(),
                input.getDocs().getName(),
                info,
                info,
                new BigDecimal("0.0"));
    }

    public BookDto bookModelConvertToBookDTO(@NotNull Book book) {
        return new BookDto(book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getSheets(),
                book.getWeight(),
                book.getCost());
    }
    public Book bookDTOConvertToBookModel(@NotNull BookDto bookDto) {
        return new Book(bookDto.getIsbn(),
                bookDto.getTitle(),
                bookDto.getAuthor(),
                bookDto.getSheets(),
                bookDto.getWeight(),
                bookDto.getCost());
    }
}
