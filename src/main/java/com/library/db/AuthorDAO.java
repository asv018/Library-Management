package com.library.db;

import com.library.api.Author;
import com.library.core.AuthorMapper;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.*;
import java.util.List;
import java.util.Optional;

@RegisterRowMapper(AuthorMapper.class)
public interface AuthorDAO {
    
    @SqlUpdate("INSERT INTO authors (name, birthdate, nationality) VALUES (:name, :birthdate, :nationality)")
    @GetGeneratedKeys
    Long createAuthor(@BindBean Author author);
    
    @SqlQuery("SELECT * FROM authors WHERE id = :id")
    Optional<Author> findById(@Bind("id") Long id);
    
    @SqlQuery("SELECT * FROM authors ORDER BY name LIMIT :limit OFFSET :offset")
    List<Author> findAll(@Bind("offset") int offset, @Bind("limit") int limit);
    
    @SqlQuery("SELECT COUNT(*) FROM authors")
    int getTotalCount();
    
    @SqlQuery("SELECT * FROM authors WHERE LOWER(name) LIKE LOWER(CONCAT('%', :query, '%')) " +
              "OR LOWER(nationality) LIKE LOWER(CONCAT('%', :query, '%')) " +
              "ORDER BY name LIMIT :limit OFFSET :offset")
    List<Author> search(@Bind("query") String query, @Bind("offset") int offset, @Bind("limit") int limit);
    
    @SqlQuery("SELECT COUNT(*) FROM authors WHERE LOWER(name) LIKE LOWER(CONCAT('%', :query, '%')) " +
              "OR LOWER(nationality) LIKE LOWER(CONCAT('%', :query, '%'))")
    int getSearchCount(@Bind("query") String query);
    
    @SqlUpdate("UPDATE authors SET name = :name, birthdate = :birthdate, nationality = :nationality WHERE id = :id")
    void updateAuthor(@BindBean Author author);
    
    @SqlUpdate("DELETE FROM authors WHERE id = :id")
    void deleteAuthor(@Bind("id") Long id);
    
    @SqlQuery("SELECT COUNT(*) FROM books WHERE author_id = :authorId")
    int getBookCount(@Bind("authorId") Long authorId);
}