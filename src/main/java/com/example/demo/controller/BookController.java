package com.example.demo.controller;

import com.example.demo.exception.BookException;
import com.example.demo.model.Book;
import com.example.demo.response.BookResponse;
import com.example.demo.sevice.BookService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("api/book")
@Tag(name = "Controller for the third task", description = "Task 3, Task 4 with deserialization")
public class BookController {

    private final BookService bookService;

    @GetMapping(value = "/booksList")
    public ResponseEntity<List<Book>> read() throws BookException {
        List<Book> books = bookService.readAll();
        if (CollectionUtils.isEmpty(books)) {
            throw new BookException("Book`s not found");
        } else
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @PostMapping(value = "/addBook")
    public ResponseEntity<?> create(@Valid @RequestBody Book book) {
        bookService.create(book);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") int id, @Valid @RequestBody Book book) throws BookException {
        final boolean updated = bookService.update(book, id);
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

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BookException.class)
    public BookResponse handleException(BookException e) {
        return new BookResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
}

