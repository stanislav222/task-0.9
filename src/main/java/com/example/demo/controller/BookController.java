package com.example.demo.controller;

import com.example.demo.exception.BookException;
import com.example.demo.external.alfabank.model.Currency;
import com.example.demo.model.dto.BookDto;
import com.example.demo.sevice.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/book")
@RequiredArgsConstructor
@Tag(name = "books", description = "the books API")
public class BookController {

    private final BookService bookService;

    @Operation(
            operationId = "booksList",
            summary = "Список книг",
            tags = { "public" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful execution of the request"),
                    @ApiResponse(responseCode = "400", description = "The list of books is empty",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation =  BookException.class)))
            }
    )
    @GetMapping(value = "/booksList",  produces = { "application/json;charset=UTF-8" })
    public ResponseEntity<List<BookDto>> read() throws BookException {
        List<BookDto> bookDtoList = bookService.readAll();
        if (CollectionUtils.isEmpty(bookDtoList)) {
            throw new BookException("Book`s not found");
        }
        return new ResponseEntity<>(bookDtoList, HttpStatus.OK);
    }

    @Operation(
            operationId = "author",
            summary = "Поиск по автору из БД и OpenLibrary",
            tags = { "public" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful execution of the request",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation =  BookDto.class))),
                    @ApiResponse(responseCode = "400", description = "The list of books is empty",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation =  BookException.class)))
            }
    )
    @GetMapping(value = "/{author}", produces = { "application/json;charset=UTF-8" })
    public ResponseEntity<List<BookDto>> read(@PathVariable String author) throws BookException {
        List<BookDto> bookDtoList = bookService.readBookByAuthorFromDbAndOL(author);
        if (CollectionUtils.isEmpty(bookDtoList)) {
            throw new BookException("Book`s not found");
        }
        return new ResponseEntity<>(bookDtoList, HttpStatus.OK);
    }

    @Operation(
            operationId = "priceByTitle",
            summary = "Поиск по названию цены книги и конвертация цены в другую валюту",
            tags = { "public" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful execution of the request",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation =  String.class))),
                    @ApiResponse(responseCode = "400", description = "The absence of a book in the database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation =  BookException.class)))
            }
    )
    @GetMapping(value = "/price/{title}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPrice(@PathVariable String title,
                                      @RequestParam(required = false) List<Currency> nameCurrency) throws BookException {
        if(CollectionUtils.isEmpty(nameCurrency)){
            return new ResponseEntity<>(bookService.getPriceByTitle(title), HttpStatus.OK);
        }
        List<Currency> currencyListFiltered = nameCurrency.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new ResponseEntity<>(bookService.getPriceByTitleWithCostInDifferentCurrencies(title,currencyListFiltered), HttpStatus.OK);
    }

    @Operation(
            operationId = "addBook",
            summary = "Добавление книги",
            tags = { "public" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful execution of the request",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation =  BookDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid json",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation =  MethodArgumentNotValidException.class)))
            }
    )
    @PostMapping(value = "/addBook")
    public ResponseEntity<?> create(@Valid @RequestBody BookDto bookDto) {
        bookService.create(bookDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(
            operationId = "updateBook",
            summary = "Обновление книги",
            tags = { "public" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful execution of the request"),
                    @ApiResponse(responseCode = "400", description = "Invalid json",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation =  MethodArgumentNotValidException.class)))
            }
    )
    @PutMapping(value = "/{id}", produces = { "application/json;charset=UTF-8" })
    public ResponseEntity<?> update(@PathVariable(name = "id") int id, @Valid @RequestBody BookDto bookDto) {
        bookService.update(bookDto, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            operationId = "deleteBook",
            summary = "Удаление книги по id",
            tags = { "public" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful execution of the request"),
                    @ApiResponse(responseCode = "400", description = "Books by id are missing in the database",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation =  BookException.class)))
            }
    )
    @DeleteMapping(value = "/{id}", produces = { "application/json;charset=UTF-8" })
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) throws BookException {
        final boolean deleted = bookService.delete(id);
            if (!deleted){
                throw new BookException("Book was not deleted");
            }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}

