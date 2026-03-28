package com.streaming.movieplatform.controller.movie;

import com.streaming.movieplatform.dto.CommentRequest;
import com.streaming.movieplatform.dto.MovieFilterRequest;
import com.streaming.movieplatform.dto.RatingRequest;
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
        model.addAttribute("genres", genreRepository.findAllByOrderByNameAsc());
        model.addAttribute("countries", countryRepository.findAllByOrderByNameAsc());
        model.addAttribute("accessLevels", com.streaming.movieplatform.enums.AccessLevel.values());
        model.addAttribute("movieTypes", com.streaming.movieplatform.enums.MovieType.values());
        return "movie/list";
    }

    @GetMapping("/{movieId}")
    public String detail(@PathVariable Long movieId, Model model) {
        Movie movie = movieService.getMovieDetails(movieId);
        User currentUser = userService.getCurrentUser();
        model.addAttribute("movie", movie);
        model.addAttribute("episodes", episodeRepository.findByMovieIdAndActiveTrueOrderByEpisodeNumberAsc(movieId));
        model.addAttribute("relatedMovies", movieService.getRelatedMovies(movie));
        model.addAttribute("comments", commentService.getVisibleComments(movieId));
        model.addAttribute("commentRequest", new CommentRequest());
        model.addAttribute("ratingRequest", new RatingRequest());
        model.addAttribute("canWatch", movieService.canWatch(currentUser, movie));
        model.addAttribute("isFavorite", movieService.isFavorite(currentUser, movieId));
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

        model.addAttribute("movie", movie);
        model.addAttribute("episodes", episodeRepository.findByMovieIdAndActiveTrueOrderByEpisodeNumberAsc(movieId));
        model.addAttribute("currentEpisode", episode);
        model.addAttribute("canWatch", canWatch);
        model.addAttribute("relatedMovies", movieService.getRelatedMovies(movie));
        return "movie/watch";
    }
}
