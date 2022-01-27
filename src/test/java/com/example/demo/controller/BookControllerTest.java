package com.example.demo.controller;

import com.example.demo.exception.BookException;
import com.example.demo.model.dto.BookDto;
import com.example.demo.sevice.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private static final List<BookDto> BOOK_DTOS = new ArrayList<>(){{
        add(new BookDto("isbn", "title", "author", "sheets", "weight", new BigDecimal("0.0")));
    }};

    @BeforeEach
    void setMockOutput() {
        lenient().when(bookService.readAll()).thenReturn(BOOK_DTOS);
        lenient().when(bookService.readBookByAuthorFromDbAndOL("rowling")).thenReturn(BOOK_DTOS);
    }

    @Test
    public void readAll() {
        ResponseEntity<List<BookDto>> response = null;
        try {
            response = bookController.read();
            assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(BOOK_DTOS);
        } catch (BookException e) {
            e.printStackTrace();
        }
    }

    @Test
    void readAllWithAuthor() {
        ResponseEntity<List<BookDto>> response = null;
        try {
            response = bookController.read("rowling");
            assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(BOOK_DTOS);
        } catch (BookException e) {
            e.printStackTrace();
        }
    }
}