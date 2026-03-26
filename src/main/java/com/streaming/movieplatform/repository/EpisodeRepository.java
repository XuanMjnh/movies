package com.streaming.movieplatform.repository;

import com.streaming.movieplatform.entity.Episode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {
    List<Episode> findByMovieIdAndActiveTrueOrderByEpisodeNumberAsc(Long movieId);
    List<Episode> findByMovieIdOrderByEpisodeNumberAsc(Long movieId);
    Optional<Episode> findFirstByMovieIdAndActiveTrueOrderByEpisodeNumberAsc(Long movieId);
}
