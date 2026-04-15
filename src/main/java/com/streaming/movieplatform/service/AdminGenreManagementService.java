package com.streaming.movieplatform.service;

import com.streaming.movieplatform.dto.AdminGenreRequest;
import com.streaming.movieplatform.entity.Genre;

import java.util.List;

public interface AdminGenreManagementService {
    List<Genre> getAllGenres();
    Genre getGenreById(Long genreId);
    Genre saveGenre(AdminGenreRequest request);
    void deleteGenre(Long genreId);
}
