package com.library.resources;

import com.library.api.Author;
import com.library.api.Book;
import com.library.db.AuthorDAO;
import com.library.db.BookDAO;
import io.dropwizard.jersey.params.IntParam;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {
    private final BookDAO bookDAO;
    private final AuthorDAO authorDAO;

    public BookResource(BookDAO bookDAO, AuthorDAO authorDAO) {
        this.bookDAO = bookDAO;
        this.authorDAO = authorDAO;
    }

    @POST
    public Response createBook(@NotNull @Valid Book book) {
        // Validate author exists
        Optional<Author> author = authorDAO.findById(book.getAuthorId());
        if (author.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Author not found"))
                    .build();
        }
        
        // Check if ISBN already exists
        Optional<Book> existingBook = bookDAO.findByIsbn(book.getIsbn());
        if (existingBook.isPresent()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("error", "Book with this ISBN already exists"))
                    .build();
        }
        
        try {
            Long id = bookDAO.createBook(book);
            book.setId(id);
            book.setAuthor(author.get());
            return Response.status(Response.Status.CREATED).entity(book).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Failed to create book: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    public Response getAllBooks(
            @QueryParam("page") @DefaultValue("1") IntParam page,
            @QueryParam("size") @DefaultValue("10") IntParam size,
            @QueryParam("search") String search,
            @QueryParam("authorId") Long authorId) {
        
        int pageNum = page.get();
        int pageSize = size.get();
        
        if (pageNum < 1 || pageSize < 1 || pageSize > 100) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Invalid pagination parameters"))
                    .build();
        }
        
        int offset = (pageNum - 1) * pageSize;
        
        List<Book> books;
        int totalCount;
        
        if (authorId != null) {
            books = bookDAO.findByAuthorId(authorId);
            totalCount = books.size();
            // Apply pagination manually for author filter
            int fromIndex = Math.min(offset, books.size());
            int toIndex = Math.min(offset + pageSize, books.size());
            books = books.subList(fromIndex, toIndex);
        } else if (search != null && !search.trim().isEmpty()) {
            books = bookDAO.search(search, offset, pageSize);
            totalCount = bookDAO.getSearchCount(search);
        } else {
            books = bookDAO.findAll(offset, pageSize);
            totalCount = bookDAO.getTotalCount();
        }
        
        // Populate author information for each book
        for (Book book : books) {
            Optional<Author> author = authorDAO.findById(book.getAuthorId());
            author.ifPresent(book::setAuthor);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", books);
        response.put("page", pageNum);
        response.put("size", pageSize);
        response.put("totalElements", totalCount);
        response.put("totalPages", (int) Math.ceil((double) totalCount / pageSize));
        
        return Response.ok(response).build();
    }

    @GET
    @Path("/{id}")
    public Response getBookById(@PathParam("id") Long id) {
        Optional<Book> book = bookDAO.findById(id);
        if (book.isPresent()) {
            Book foundBook = book.get();
            Optional<Author> author = authorDAO.findById(foundBook.getAuthorId());
            author.ifPresent(foundBook::setAuthor);
            return Response.ok(foundBook).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "Book not found"))
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response updateBook(@PathParam("id") Long id, @NotNull @Valid Book book) {
        Optional<Book> existing = bookDAO.findById(id);
        if (existing.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Book not found"))
                    .build();
        }
        
        // Validate author exists
        Optional<Author> author = authorDAO.findById(book.getAuthorId());
        if (author.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Author not found"))
                    .build();
        }
        
        // Check if ISBN is being changed and already exists
        if (!existing.get().getIsbn().equals(book.getIsbn())) {
            Optional<Book> bookWithIsbn = bookDAO.findByIsbn(book.getIsbn());
            if (bookWithIsbn.isPresent()) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(Map.of("error", "Book with this ISBN already exists"))
                        .build();
            }
        }
        
        book.setId(id);
        try {
            bookDAO.updateBook(book);
            book.setAuthor(author.get());
            return Response.ok(book).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Failed to update book: " + e.getMessage()))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteBook(@PathParam("id") Long id) {
        Optional<Book> existing = bookDAO.findById(id);
        if (existing.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Book not found"))
                    .build();
        }
        
        try {
            bookDAO.deleteBook(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to delete book: " + e.getMessage()))
                    .build();
        }
    }
}