package com.example.demo.dao;

import com.example.demo.model.Book;

import java.util.List;

public interface BookDao {
    
    String insertSql = "insert into book (isbn, title, author, sheets, weight, cost) values (?, ?, ?, ?, ?, ?)";
    String selectSql = "select * from book where deleted is null limit 10";
    String updateSql = "update book set isbn = ?, title = ?, author = ?, sheets = ?, weight =?, cost= ? where id = ?";
    String deleteSql = "update book set deleted = 1  where id = ?";
    String selectSqlByAuthor = "select * from book where author ilike ? and deleted is null limit 10";

    void createBook(Book Book);
    List<Book> readAll();
    boolean update(Book Book, int id);
    boolean delete(int id);
    List<Book> getBookByAuthor(String author);
}
