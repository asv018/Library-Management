package com.library.resources;

import com.library.api.Author;
import com.library.db.AuthorDAO;
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

@Path("/authors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthorResource {
    private final AuthorDAO authorDAO;

    public AuthorResource(AuthorDAO authorDAO) {
        this.authorDAO = authorDAO;
    }

    @POST
    public Response createAuthor(@NotNull @Valid Author author) {
        try {
            Long id = authorDAO.createAuthor(author);
            author.setId(id);
            return Response.status(Response.Status.CREATED).entity(author).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Failed to create author: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    public Response getAllAuthors(
            @QueryParam("page") @DefaultValue("1") IntParam page,
            @QueryParam("size") @DefaultValue("10") IntParam size,
            @QueryParam("search") String search) {
        
        int pageNum = page.get();
        int pageSize = size.get();
        
        if (pageNum < 1 || pageSize < 1 || pageSize > 100) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Invalid pagination parameters"))
                    .build();
        }
        
        int offset = (pageNum - 1) * pageSize;
        
        List<Author> authors;
        int totalCount;
        
        if (search != null && !search.trim().isEmpty()) {
            authors = authorDAO.search(search, offset, pageSize);
            totalCount = authorDAO.getSearchCount(search);
        } else {
            authors = authorDAO.findAll(offset, pageSize);
            totalCount = authorDAO.getTotalCount();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", authors);
        response.put("page", pageNum);
        response.put("size", pageSize);
        response.put("totalElements", totalCount);
        response.put("totalPages", (int) Math.ceil((double) totalCount / pageSize));
        
        return Response.ok(response).build();
    }

    @GET
    @Path("/{id}")
    public Response getAuthorById(@PathParam("id") Long id) {
        Optional<Author> author = authorDAO.findById(id);
        if (author.isPresent()) {
            return Response.ok(author.get()).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "Author not found"))
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response updateAuthor(@PathParam("id") Long id, @NotNull @Valid Author author) {
        Optional<Author> existing = authorDAO.findById(id);
        if (existing.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Author not found"))
                    .build();
        }
        
        author.setId(id);
        try {
            authorDAO.updateAuthor(author);
            return Response.ok(author).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Failed to update author: " + e.getMessage()))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteAuthor(@PathParam("id") Long id) {
        Optional<Author> existing = authorDAO.findById(id);
        if (existing.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Author not found"))
                    .build();
        }
        
        // Check if author has books
        int bookCount = authorDAO.getBookCount(id);
        if (bookCount > 0) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("error", "Cannot delete author with existing books"))
                    .build();
        }
        
        try {
            authorDAO.deleteAuthor(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to delete author: " + e.getMessage()))
                    .build();
        }
    }
}