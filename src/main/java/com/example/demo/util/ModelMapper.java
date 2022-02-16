package com.example.demo.util;

import com.example.demo.external.openlibrary.dto.BookFromOpenLibraryDto;
import com.example.demo.model.Book;
import com.example.demo.model.dto.BookDto;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Component
public class ModelMapper {
    public BookDto openLibraryDtoConvertToBookDTO(@NotNull BookFromOpenLibraryDto input) {
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
