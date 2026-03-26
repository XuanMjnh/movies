package com.streaming.movieplatform.dto;

import jakarta.validation.constraints.NotBlank;

public class AdminGenreRequest {

    private Long id;

    @NotBlank(message = "Tên thể loại không được để trống")
    private String name;

    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
