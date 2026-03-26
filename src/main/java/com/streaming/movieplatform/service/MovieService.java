package com.streaming.movieplatform.service;

import com.streaming.movieplatform.dto.AdminEpisodeRequest;
import com.streaming.movieplatform.dto.AdminMovieRequest;
import com.streaming.movieplatform.dto.MovieFilterRequest;
import com.streaming.movieplatform.entity.Episode;
import com.streaming.movieplatform.entity.Favorite;
import com.streaming.movieplatform.entity.Movie;
import com.streaming.movieplatform.entity.User;
import com.streaming.movieplatform.entity.WatchHistory;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface MovieService {
    Map<String, List<Movie>> getHomeSections(User currentUser);
    Page<Movie> searchMovies(MovieFilterRequest filter);
    Movie getMovieDetails(Long movieId);
    Episode resolveEpisode(Long movieId, Long episodeId);
    boolean canWatch(User user, Movie movie);
    void saveWatchProgress(User user, Long episodeId, Integer positionSeconds, boolean completed);
    List<Movie> getRelatedMovies(Movie movie);
    boolean toggleFavorite(User user, Long movieId);
    boolean isFavorite(User user, Long movieId);
    List<Favorite> getFavoriteMovies(User user);
    List<WatchHistory> getWatchHistory(User user);
    void deleteHistoryItem(User user, Long historyId);
    void clearWatchHistory(User user);
    void submitRating(User user, Long movieId, Integer stars);
    Movie saveMovie(AdminMovieRequest request);
    Episode saveEpisode(AdminEpisodeRequest request);
    void deleteMovie(Long movieId);
    void deleteEpisode(Long episodeId);
}
