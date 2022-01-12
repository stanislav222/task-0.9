package com.example.demo.sevice;

import com.example.demo.model.Book;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class BookService {

    private static final Map<Integer, Book> BOOK_STORAGE = new HashMap<>();
    private static final AtomicInteger ID_SYNC = new AtomicInteger();

    public void create(Book book) {
        final int bookId = ID_SYNC.incrementAndGet();
        book.setId(bookId);
        BOOK_STORAGE.put(bookId, book);
    }

    public List<Book> readAll() {
        return new ArrayList<>(BOOK_STORAGE.values());
    }

    public Book read(int id) {
        return BOOK_STORAGE.get(id);
    }

    public boolean update(Book book, int id) {
        if (BOOK_STORAGE.containsKey(id)) {
            book.setId(id);
            BOOK_STORAGE.put(id, book);
            return true;
        }
        return false;
    }

    public boolean delete(int id) {
        return BOOK_STORAGE.remove(id) != null;
    }
}
