package com.library.core;

import com.library.api.Author;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthorMapper implements RowMapper<Author> {
    @Override
    public Author map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new Author(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getDate("birthdate") != null ? rs.getDate("birthdate").toLocalDate() : null,
            rs.getString("nationality")
        );
    }
}