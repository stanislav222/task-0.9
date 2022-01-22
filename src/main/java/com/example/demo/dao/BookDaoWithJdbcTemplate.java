package com.example.demo.dao;

import com.example.demo.model.Book;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookDaoWithJdbcTemplate {

    private static final Logger logger = LoggerFactory.getLogger(BookDaoWithJdbcTemplate.class);

    private final JdbcTemplate jdbcTemplate;

    private final String insertSql = "insert into book (isbn, title, author, sheets, weight, cost) values (?, ?, ?, ?, ?, ?)";
    private final String selectSql = "select * from book where deleted is null limit 10";
    private final String updateSql = "update book set isbn = ?, title = ?, author = ?, sheets = ?, weight =?, cost= ? where id = ?";
    private final String deleteSql = "update book set deleted = 1  where id = ?";

    public void createBook(Book book) {
        int result =  jdbcTemplate.update(insertSql,
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getSheets(),
                book.getWeight(),
                book.getCost());
        if (result != 1){
            logger.error("Can't create book");
        }
        logger.info("Book was create");
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
            logger.error("Can't update book");
            return false;
        }
        logger.info("Book was update");
        return true;
    }

    public boolean delete(int id) {
        int result =  jdbcTemplate.update(deleteSql, id);
        if (result != 1) {
            logger.error("Can't delete book");
            return false;
        }
        logger.info("Book was delete");
        return true;
    }
}
