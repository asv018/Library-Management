package com.library.db;

import com.library.api.Book;
import com.library.core.BookMapper;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.*;
import java.util.List;
import java.util.Optional;

@RegisterRowMapper(BookMapper.class)
public interface BookDAO {
    
    @SqlUpdate("INSERT INTO books (title, author_id, published_date, isbn) VALUES (:title, :authorId, :publishedDate, :isbn)")
    @GetGeneratedKeys
    Long createBook(@BindBean Book book);
    
    @SqlQuery("SELECT * FROM books WHERE id = :id")
    Optional<Book> findById(@Bind("id") Long id);
    
    @SqlQuery("SELECT * FROM books ORDER BY title LIMIT :limit OFFSET :offset")
    List<Book> findAll(@Bind("offset") int offset, @Bind("limit") int limit);
    
    @SqlQuery("SELECT COUNT(*) FROM books")
    int getTotalCount();
    
    @SqlQuery("SELECT * FROM books WHERE LOWER(title) LIKE LOWER(CONCAT('%', :query, '%')) " +
              "OR LOWER(isbn) LIKE LOWER(CONCAT('%', :query, '%')) " +
              "ORDER BY title LIMIT :limit OFFSET :offset")
    List<Book> search(@Bind("query") String query, @Bind("offset") int offset, @Bind("limit") int limit);
    
    @SqlQuery("SELECT COUNT(*) FROM books WHERE LOWER(title) LIKE LOWER(CONCAT('%', :query, '%')) " +
              "OR LOWER(isbn) LIKE LOWER(CONCAT('%', :query, '%'))")
    int getSearchCount(@Bind("query") String query);
    
    @SqlQuery("SELECT * FROM books WHERE author_id = :authorId ORDER BY title")
    List<Book> findByAuthorId(@Bind("authorId") Long authorId);
    
    @SqlUpdate("UPDATE books SET title = :title, author_id = :authorId, published_date = :publishedDate, isbn = :isbn WHERE id = :id")
    void updateBook(@BindBean Book book);
    
    @SqlUpdate("DELETE FROM books WHERE id = :id")
    void deleteBook(@Bind("id") Long id);
    
    @SqlQuery("SELECT * FROM books WHERE isbn = :isbn")
    Optional<Book> findByIsbn(@Bind("isbn") String isbn);
}