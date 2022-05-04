package com.intervale.cources.dao;

import com.intervale.cources.model.Book;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BookDaoWithJdbcTemplate implements BookDao{

    private final JdbcTemplate jdbcTemplate;

    public boolean createBook(Book book) {
        int result =  jdbcTemplate.update(insertSql,
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getSheets(),
                book.getWeight(),
                book.getCost());
        if (result != 1){
            log.error("Can't create book {}", book);
            return false;
        }
        log.info("Book was create {}", book);
        return true;
    }

    public List<Book> readAll() {
        return jdbcTemplate.query(selectSql, new BeanPropertyRowMapper<>(Book.class));
    }

    public boolean update(Book book, int id) {
        int result = jdbcTemplate.update(updateSql, book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getSheets(),
                book.getWeight(),
                book.getCost(),
                id);
        if (result != 1) {
            log.error("Can't update book by id: {}", id);
            return false;
        }
        log.info("Book was update by id: {}", id);
        return true;
    }

    public boolean delete(int id) {
        int result =  jdbcTemplate.update(deleteSql, id);
        if (result != 1) {
            log.error("Can't delete book by id: {}", id);
            return false;
        }
        log.info("Book was delete by id: {}", id);
        return true;
    }

    public List<Book> getBookByAuthor(String author) {
        return jdbcTemplate.query(selectSqlByAuthor,
                ps -> ps.setString(1, "%" + author + "%"),
                new BeanPropertyRowMapper<>(Book.class));
    }

    @Override
    public Book getPriceByTitle(String title) {
        try {
            Book book = jdbcTemplate.queryForObject(selectBookByTitle,
                    new BeanPropertyRowMapper<>(Book.class),
                    "%" + title + "%");
            log.info("Prices successfully received by title: {}", title);
            return book;
        } catch (EmptyResultDataAccessException e) {
            log.error("Price not found by title: {}", title);
            return null;
        }
    }
}
