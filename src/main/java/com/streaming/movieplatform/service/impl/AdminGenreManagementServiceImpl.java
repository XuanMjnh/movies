package com.streaming.movieplatform.service.impl;

import com.streaming.movieplatform.dto.AdminGenreRequest;
import com.streaming.movieplatform.entity.Genre;
import com.streaming.movieplatform.exception.ResourceNotFoundException;
import com.streaming.movieplatform.repository.GenreRepository;
import com.streaming.movieplatform.service.AdminGenreManagementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminGenreManagementServiceImpl implements AdminGenreManagementService {

    private final GenreRepository genreRepository;

    public AdminGenreManagementServiceImpl(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Genre> getAllGenres() {
        return genreRepository.findAllByOrderByNameAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Genre getGenreById(Long genreId) {
        return genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay the loai"));
    }

    @Override
    public Genre saveGenre(AdminGenreRequest request) {
        Genre genre = request.getId() == null ? new Genre() : getGenreById(request.getId());
        genre.setName(request.getName());
        genre.setDescription(request.getDescription());
        return genreRepository.save(genre);
    }

    @Override
    public void deleteGenre(Long genreId) {
        genreRepository.deleteById(genreId);
    }
}
