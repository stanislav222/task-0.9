package com.example.demo.sevice;

import com.example.demo.dao.BookDaoWithJdbcTemplate;
import com.example.demo.model.Book;
import com.example.demo.model.dto.BookDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookDaoWithJdbcTemplate bookDaoWithJdbcTemplate;

    @Mock
    private OpenLibraryExchangeClient openLibraryExchangeClient;

    @InjectMocks
    private BookService bookService;

    @Test
    void bookModelConvertToBookDTO() {
        BookDto dto = new BookDto("isbn", "title", "author", "sheets", "weight", new BigDecimal("0.0"));
        Book book = bookService.bookDTOConvertToBookModel(dto);
        assertThat(book.getIsbn(), equalTo("isbn"));
        assertThat(book.getTitle(), equalTo("title"));
        assertThat(book.getAuthor(), equalTo("author"));
        assertThat(book.getSheets(), equalTo("sheets"));
        assertThat(book.getWeight(), equalTo("weight"));
        assertThat(book.getCost(), comparesEqualTo(new BigDecimal("0.0")));
    }
}