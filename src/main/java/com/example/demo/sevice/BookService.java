package com.example.demo.sevice;

import com.example.demo.dao.BookDao;
import com.example.demo.model.Book;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BookService {

    private final BookDao bookDao;

    public void create(Book book) {
        bookDao.createBook(book);
    }

    public List<Book> readAll() {
        return bookDao.readAll();
    }

    public boolean update(Book book, int id) {
        return bookDao.update(book, id);
    }

    public boolean delete(int id) {
        return bookDao.delete(id);
    }

    public BookService(BookDao bookDao) {
        this.bookDao = bookDao;
    }
}
