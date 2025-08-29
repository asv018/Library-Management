package com.library.core;

import com.library.api.Book;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookMapper implements RowMapper<Book> {
    @Override
    public Book map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new Book(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getLong("author_id"),
            rs.getDate("published_date") != null ? rs.getDate("published_date").toLocalDate() : null,
            rs.getString("isbn")
        );
    }
}