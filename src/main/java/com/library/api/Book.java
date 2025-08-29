package com.library.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public class Book {
    private Long id;
    
    @NotNull
    @Size(min = 1, max = 500)
    private String title;
    
    @NotNull
    private Long authorId;
    
    private LocalDate publishedDate;
    
    @NotNull
    @Pattern(regexp = "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$", 
             message = "Invalid ISBN format")
    private String isbn;
    
    private Author author;

    public Book() {}

    public Book(Long id, String title, Long authorId, LocalDate publishedDate, String isbn) {
        this.id = id;
        this.title = title;
        this.authorId = authorId;
        this.publishedDate = publishedDate;
        this.isbn = isbn;
    }

    @JsonProperty
    public Long getId() {
        return id;
    }

    @JsonProperty
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty
    public String getTitle() {
        return title;
    }

    @JsonProperty
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty
    public Long getAuthorId() {
        return authorId;
    }

    @JsonProperty
    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    @JsonProperty
    public LocalDate getPublishedDate() {
        return publishedDate;
    }

    @JsonProperty
    public void setPublishedDate(LocalDate publishedDate) {
        this.publishedDate = publishedDate;
    }

    @JsonProperty
    public String getIsbn() {
        return isbn;
    }

    @JsonProperty
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    @JsonProperty
    public Author getAuthor() {
        return author;
    }

    @JsonProperty
    public void setAuthor(Author author) {
        this.author = author;
    }
}