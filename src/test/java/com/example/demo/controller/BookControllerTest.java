package com.example.demo.controller;

import com.example.demo.config.WebConfig;
import com.example.demo.exception.BookException;
import com.example.demo.external.alfabank.model.Currency;
import com.example.demo.interceptor.SendingToKafkaInterceptor;
import com.example.demo.model.dto.BookDto;
import com.example.demo.sevice.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebConfig webConfig;

    @MockBean
    private SendingToKafkaInterceptor kafkaInterceptor;

    @MockBean
    private BookService bookService;

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
    public void readAll() throws Exception {
        when(bookService.readAll()).thenReturn(List.of(dtoForTest));
        this.mvc.perform(get("/api/v1/book/booksList")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].isbn").isNotEmpty())
                .andExpect(jsonPath("$.[0].title").isNotEmpty())
                .andExpect(jsonPath("$.[0].author").isNotEmpty())
                .andExpect(jsonPath("$.[0].sheets").isNotEmpty())
                .andExpect(jsonPath("$.[0].weight").isNotEmpty())
                .andExpect(jsonPath("$.[0].cost").isNotEmpty())
                .andExpect(status().isOk());
    }

    @Test
    void createBook() throws Exception {
        this.mvc.perform(post("/api/v1/book/addBook")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(filledJson))
                .andExpect(status().isCreated());
        verify(bookService).create(any(BookDto.class));
    }

    @Test
    void createFailsWhenParamsEmpty() throws Exception {
        mvc.perform(post("/api/v1/book/addBook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(emptyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBook() throws Exception {
        mvc.perform(put("/api/v1/book/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(filledJson))
                .andExpect(status().isOk());
        verify(bookService).update(any(BookDto.class), anyInt());
    }

    @Test
    void updateFailsWhenParamsEmpty() throws Exception {
        mvc.perform(put("/api/v1/book/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(emptyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void readAllWithAuthor() throws Exception {
        when(bookService.readBookByAuthorFromDbAndOL(anyString())).thenReturn(List.of(dtoForTest));
        this.mvc.perform(get("/api/v1/book/{author}", "test"))
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
        this.mvc.perform(get("/api/v1/book/booksList")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"message\": \"Book`s not found\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void readAllWithAuthorAndException() throws Exception {
        when(bookService.readBookByAuthorFromDbAndOL(anyString())).thenReturn(null);
        this.mvc.perform(get("/api/v1/book/{author}", "test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"message\": \"Book`s not found\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteBook() throws Exception {
        when(bookService.delete(anyInt())).thenReturn(true);
        this.mvc.perform(delete("/api/v1/book/{id}", 1)).andExpect(status().isOk());
    }

    @Test
    void deleteBookWithException() throws Exception {
        when(bookService.delete(anyInt())).thenReturn(false);
        this.mvc.perform(delete("/api/v1/book/{id}", 1))
                .andExpect(content().json("{\"message\": \"Book was not deleted\"}"))
                .andExpect(status().isBadRequest());
    }

    //TODO: не работаеют все тесты ниже MockHttpServletResponse: пустое body
    /*
        MockHttpServletResponse:
           Status = 200
        Error message = null
          Headers = []
        Content type = null
             Body =
        Forwarded URL = null
        Redirected URL = null
          Cookies = []
     */
    @Test
    void getPrice() throws Exception {
       when(bookService.getPriceByTitle("title")).thenReturn(new BigDecimal("1.11"));
        this.mvc.perform(get("/api/v1/book/price/{title}", "title")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().string("1.11"))
                .andExpect(status().isOk());
       verify(bookService).getPriceByTitle("title");
    }

    @Test
    void getPriceWithException() throws Exception {
        when(bookService.getPriceByTitle("title")).thenThrow(new BookException("Test"));
        this.mvc.perform(get("/api/v1/book/price/{title}", "title")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\": \"Test\"}"));
    }

   @Test
    void getPriceWithCurrency() throws Exception {
        when(bookService.getPriceByTitleWithCostInDifferentCurrencies("title", List.of(Currency.RUB)
                )).thenReturn("test string");
        this.mvc.perform(get("/api/v1/book/price/test?nameCurrency=RUB")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("test string"));
    }

    @Test
    void getPriceWithCurrencyWithException() throws Exception {
        when(bookService.getPriceByTitleWithCostInDifferentCurrencies("title", List.of(Currency.RUB)
        )).thenThrow(new BookException("Test"));
        this.mvc.perform(get("/api/v1/book/price/test?nameCurrency=RUB")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\": \"Test\"}"));
    }
}
