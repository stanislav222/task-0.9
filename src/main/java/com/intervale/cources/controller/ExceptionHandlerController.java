package com.intervale.cources.controller;

import com.intervale.cources.exception.BookException;
import com.intervale.cources.exception.NationalBankException;
import com.intervale.cources.exception.OpenLibException;
import com.intervale.cources.response.BookResponse;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(BookException.class)
    public ResponseEntity<BookResponse> handleExceptionBookCreat(BookException e) {
        return ResponseEntity.badRequest().body(new BookResponse(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptionsSimple(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<String> handleConflict(RuntimeException ex) {
        return new ResponseEntity<>("{\n" +
                "  \"message\": \"There are three types of currencies available: " +
                "RUB, EUR, USD. Request example - /{books title}?nameCurrency=RUB, USD\"\n" +
                "}", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NationalBankException.class)
    public ResponseEntity<String> handleConflict(NationalBankException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(OpenLibException.class)
    public ResponseEntity<String> handleConflict(OpenLibException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
