package com.library.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public class Author {
    private Long id;
    
    @NotNull
    @Size(min = 1, max = 255)
    private String name;
    
    private LocalDate birthdate;
    
    @Size(max = 100)
    private String nationality;

    public Author() {}

    public Author(Long id, String name, LocalDate birthdate, String nationality) {
        this.id = id;
        this.name = name;
        this.birthdate = birthdate;
        this.nationality = nationality;
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
    public String getName() {
        return name;
    }

    @JsonProperty
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public LocalDate getBirthdate() {
        return birthdate;
    }

    @JsonProperty
    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    @JsonProperty
    public String getNationality() {
        return nationality;
    }

    @JsonProperty
    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
}