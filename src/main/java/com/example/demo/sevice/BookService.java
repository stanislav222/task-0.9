package com.example.demo.sevice;

import com.example.demo.entity.Book;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class BookService {

    private static final Map<Integer, Book> bookStorage = new HashMap<>();
    private static final AtomicInteger boolIdSync = new AtomicInteger();

    public void create(Book book) {
        final int bookId = boolIdSync.incrementAndGet();
        book.setId(bookId);
        bookStorage.put(bookId, book);
    }

    public List<Book> readAll() {
        return new ArrayList<>(bookStorage.values());
    }

    public Book read(int id) {
        return bookStorage.get(id);
    }

    public boolean update(Book book, int id) {
        if (bookStorage.containsKey(id)) {
            book.setId(id);
            bookStorage.put(id, book);
            return true;
        }
        return false;
    }

    public boolean delete(int id) {
        return bookStorage.remove(id) != null;
    }
}
