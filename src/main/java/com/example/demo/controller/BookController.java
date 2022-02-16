package com.example.demo.controller;

import com.example.demo.exception.BookException;
import com.example.demo.external.alfabank.model.Currency;
import com.example.demo.model.dto.BookDto;
import com.example.demo.sevice.BookService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/book")
@RequiredArgsConstructor
@Tag(name = "Controller for the third task", description = "Task 3, Task 4 with deserialization")
public class BookController {

    private final BookService bookService;

    @GetMapping(value = "/booksList")
    public ResponseEntity<List<BookDto>> read() throws BookException {
        List<BookDto> bookDtoList = bookService.readAll();
        if (CollectionUtils.isEmpty(bookDtoList)) {
            throw new BookException("Book`s not found");
        }
        return new ResponseEntity<>(bookDtoList, HttpStatus.OK);
    }

    @GetMapping(value = "/{author}")
    public ResponseEntity<List<BookDto>> read(@PathVariable String author) throws BookException {
        List<BookDto> bookDtoList = bookService.readBookByAuthorFromDbAndOL(author);
        if (CollectionUtils.isEmpty(bookDtoList)) {
            throw new BookException("Book`s not found");
        }
        return new ResponseEntity<>(bookDtoList, HttpStatus.OK);
    }

    @GetMapping(value = "/price/{title}")
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

    @PostMapping(value = "/addBook")
    public ResponseEntity<?> create(@Valid @RequestBody BookDto bookDto) {
        bookService.create(bookDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") int id, @Valid @RequestBody BookDto bookDto) {
        bookService.update(bookDto, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) throws BookException {
        final boolean deleted = bookService.delete(id);
            if (!deleted){
                throw new BookException("Book was not deleted");
            }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}

