package com.example.demo.sevice;

import com.example.demo.dao.BookDao;
import com.example.demo.dao.BookDaoWithJdbcTemplate;
import com.example.demo.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class BookService {

    private final BookDaoWithJdbcTemplate bookDaoWithJdbcTemplate;

    private final BookDao bookDao;

    public void create(Book book) {
       bookDaoWithJdbcTemplate.createBook(book);
    }

    public List<Book> readAll() {
        return bookDaoWithJdbcTemplate.readAll();
    }

    public boolean update(Book book, int id) {
        return bookDaoWithJdbcTemplate.update(book, id);
    }

    public boolean delete(int id) {
        return bookDaoWithJdbcTemplate.delete(id);
    }

}
