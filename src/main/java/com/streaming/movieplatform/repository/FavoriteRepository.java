package com.streaming.movieplatform.repository;

import com.streaming.movieplatform.entity.Favorite;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    boolean existsByUserIdAndMovieId(Long userId, Long movieId);
    void deleteByUserIdAndMovieId(Long userId, Long movieId);

    @EntityGraph(attributePaths = {"movie", "movie.country"})
    List<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId);
}
