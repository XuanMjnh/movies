package com.streaming.movieplatform.repository;

import com.streaming.movieplatform.entity.WatchHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long> {
    @EntityGraph(attributePaths = {"movie", "movie.country", "episode"})
    List<WatchHistory> findByUserIdOrderByLastWatchedAtDesc(Long userId);

    Optional<WatchHistory> findByUserIdAndEpisodeId(Long userId, Long episodeId);
    void deleteByUserIdAndId(Long userId, Long id);
    void deleteByEpisodeId(Long episodeId);
    void deleteByMovieId(Long movieId);
}
