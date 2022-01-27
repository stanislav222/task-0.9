package com.example.demo.dao;

import com.example.demo.model.Book;
import com.example.demo.model.dto.BookDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

import static com.example.demo.dao.BookDao.insertSql;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class BookDaoWithJdbcTemplateTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private BookDaoWithJdbcTemplate bookDaoWithJdbcTemplate;

    private static final Book book = new Book();

    @BeforeEach
    void setMockOutput() {
        lenient().when(jdbcTemplate.update(insertSql,
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getSheets(),
                book.getWeight(),
                book.getCost())).thenReturn(1);
    }

    @Test
    void createBook() {
         bookDaoWithJdbcTemplate.createBook(new Book("isbn",
                "title",
                "author",
                "sheets",
                "weight",
                new BigDecimal("0.0")));
    }
}