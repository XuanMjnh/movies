package com.streaming.movieplatform.controller.movie;

import com.streaming.movieplatform.dto.CommentRequest;
import com.streaming.movieplatform.dto.MovieFilterRequest;
import com.streaming.movieplatform.dto.RatingRequest;
import com.streaming.movieplatform.entity.Episode;
import com.streaming.movieplatform.entity.Movie;
import com.streaming.movieplatform.entity.User;
import com.streaming.movieplatform.repository.CountryRepository;
import com.streaming.movieplatform.repository.EpisodeRepository;
import com.streaming.movieplatform.repository.GenreRepository;
import com.streaming.movieplatform.service.CommentService;
import com.streaming.movieplatform.service.MovieService;
import com.streaming.movieplatform.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;
    private final UserService userService;
    private final GenreRepository genreRepository;
    private final CountryRepository countryRepository;
    private final EpisodeRepository episodeRepository;
    private final CommentService commentService;

    public MovieController(MovieService movieService,
                           UserService userService,
                           GenreRepository genreRepository,
                           CountryRepository countryRepository,
                           EpisodeRepository episodeRepository,
                           CommentService commentService) {
        this.movieService = movieService;
        this.userService = userService;
        this.genreRepository = genreRepository;
        this.countryRepository = countryRepository;
        this.episodeRepository = episodeRepository;
        this.commentService = commentService;
    }

    @GetMapping
    public String list(@ModelAttribute("filter") MovieFilterRequest filter, Model model) {
        model.addAttribute("moviePage", movieService.searchMovies(filter));
        populateMovieListOptions(model);
        return "movie/list";
    }

    @GetMapping("/{movieId}")
    public String detail(@PathVariable Long movieId, Model model) {
        Movie movie = movieService.getMovieDetails(movieId);
        User currentUser = userService.getCurrentUser();
        populateMovieDetailModel(model, movie, currentUser);
        return "movie/detail";
    }

    @GetMapping("/{movieId}/watch")
    public String watch(@PathVariable Long movieId,
                        @RequestParam(required = false) Long episodeId,
                        Model model) {
        Movie movie = movieService.getMovieDetails(movieId);
        User currentUser = userService.getCurrentUser();
        boolean canWatch = movieService.canWatch(currentUser, movie);
        var episode = movieService.resolveEpisode(movieId, episodeId);

        if (canWatch && currentUser != null) {
            movieService.saveWatchProgress(currentUser, episode.getId(), 0, false);
        }

        populateMovieWatchModel(model, movie, movieId, episode, canWatch);
        return "movie/watch";
    }

    private void populateMovieListOptions(Model model) {
        model.addAttribute("genres", genreRepository.findAllByOrderByNameAsc());
        model.addAttribute("countries", countryRepository.findAllByOrderByNameAsc());
        model.addAttribute("accessLevels", com.streaming.movieplatform.enums.AccessLevel.values());
        model.addAttribute("movieTypes", com.streaming.movieplatform.enums.MovieType.values());
    }

    private void populateMovieDetailModel(Model model, Movie movie, User currentUser) {
        model.addAttribute("movie", movie);
        model.addAttribute("episodes", episodeRepository.findByMovieIdAndActiveTrueOrderByEpisodeNumberAsc(movie.getId()));
        model.addAttribute("relatedMovies", movieService.getRelatedMovies(movie));
        model.addAttribute("comments", commentService.getVisibleComments(movie.getId()));
        model.addAttribute("commentRequest", new CommentRequest());
        model.addAttribute("ratingRequest", new RatingRequest());
        model.addAttribute("canWatch", movieService.canWatch(currentUser, movie));
        model.addAttribute("isFavorite", movieService.isFavorite(currentUser, movie.getId()));
    }

    private void populateMovieWatchModel(Model model, Movie movie, Long movieId, Episode currentEpisode, boolean canWatch) {
        model.addAttribute("movie", movie);
        model.addAttribute("episodes", episodeRepository.findByMovieIdAndActiveTrueOrderByEpisodeNumberAsc(movieId));
        model.addAttribute("currentEpisode", currentEpisode);
        model.addAttribute("canWatch", canWatch);
        model.addAttribute("relatedMovies", movieService.getRelatedMovies(movie));
    }
}
