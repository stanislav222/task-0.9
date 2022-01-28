package com.example.demo.controller;

import com.example.demo.model.dto.BookDto;
import com.example.demo.sevice.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;


@WebMvcTest(BookController.class)
class BookControllerTest {

    @MockBean
    private BookService bookService;

    @Autowired
    private MockMvc mvc;

    private final BookDto dtoForTest = new BookDto("isbn", "title", "author", "sheets", "weight", new BigDecimal("0.0"));
    private final String emptyJson = "{\n" +
            "       \"isbn\": \"\",\n" +
            "       \"title\": \"\",\n" +
            "       \"name\": \"\",\n" +
            "       \"surname\": \"\",\n" +
            "       \"sheets\": \"\",\n" +
            "       \"weight\": \"\",\n" +
            "       \"cost\": \n" +
            "}";
    private final String filledJson = "{\n" +
            "       \"isbn\": \"3-331-13-131\",\n" +
            "       \"title\": \"Hello world\",\n" +
            "       \"name\": \"Ivan\",\n" +
            "       \"surname\": \"Ivanov\",\n" +
            "       \"sheets\": \"200\",\n" +
            "       \"weight\": \"200 gram\",\n" +
            "       \"cost\": 32.22\n" +
            "}";

    @Test
    void createBook() throws Exception {
        bookService.create(dtoForTest);
        mvc.perform(post("/api/book/addBook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(filledJson))
                .andExpect(status().isCreated());
    }

    @Test
    void createFailsWhenParamsEmpty() throws Exception {
        mvc.perform(post("/api/book/addBook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(emptyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBook() throws Exception {
        bookService.update(dtoForTest, 1);
        mvc.perform(put("/api/book/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(filledJson))
                .andExpect(status().isOk());
    }

    @Test
    void updateFailsWhenParamsEmpty() throws Exception {
        mvc.perform(put("/api/book/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(emptyJson))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void readAll() throws Exception {
        when(bookService.readAll()).thenReturn(List.of(dtoForTest));
        mvc.perform(get("/api/book/booksList"))
                .andExpect(jsonPath("$.[0].isbn").isNotEmpty())
                .andExpect(jsonPath("$.[0].title").isNotEmpty())
                .andExpect(jsonPath("$.[0].author").isNotEmpty())
                .andExpect(jsonPath("$.[0].sheets").isNotEmpty())
                .andExpect(jsonPath("$.[0].weight").isNotEmpty())
                .andExpect(jsonPath("$.[0].cost").isNotEmpty())
                .andExpect(status().isOk());
    }

    @Test
    void readAllWithAuthor() throws Exception {
        when(bookService.readBookByAuthorFromDbAndOL(anyString())).thenReturn(List.of(dtoForTest));
        mvc.perform(get("/api/book/{author}", "test"))
                .andExpect(jsonPath("$.[0].isbn").isNotEmpty())
                .andExpect(jsonPath("$.[0].title").isNotEmpty())
                .andExpect(jsonPath("$.[0].author").isNotEmpty())
                .andExpect(jsonPath("$.[0].sheets").isNotEmpty())
                .andExpect(jsonPath("$.[0].weight").isNotEmpty())
                .andExpect(jsonPath("$.[0].cost").isNotEmpty())
                .andExpect(status().isOk());
    }

    @Test
    public void readAllWithException() throws Exception {
        when(bookService.readAll()).thenReturn(null);
        mvc.perform(get("/api/book/booksList"))
                .andExpect(content().json("{\"message\": \"Book`s not found\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void readAllWithAuthorAndException() throws Exception {
        when(bookService.readBookByAuthorFromDbAndOL(anyString())).thenReturn(null);
        mvc.perform(get("/api/book/{author}", "test"))
                .andExpect(content().json("{\"message\": \"Book`s not found\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteBook() throws Exception {
        when(bookService.delete(anyInt())).thenReturn(true);
        mvc.perform(delete("/api/book/{id}", 1)).andExpect(status().isOk());
    }

    @Test
    void deleteBookWithException() throws Exception {
        when(bookService.delete(anyInt())).thenReturn(false);
        mvc.perform(delete("/api/book/{id}", 1))
                .andExpect(content().json("{\"message\": \"Book was not deleted\"}"))
                .andExpect(status().isBadRequest());
    }
}
