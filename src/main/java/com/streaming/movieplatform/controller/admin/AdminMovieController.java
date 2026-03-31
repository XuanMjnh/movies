package com.streaming.movieplatform.controller.admin;

import com.streaming.movieplatform.dto.AdminEpisodeRequest;
import com.streaming.movieplatform.dto.AdminMovieRequest;
import com.streaming.movieplatform.entity.Episode;
import com.streaming.movieplatform.entity.Movie;
import com.streaming.movieplatform.exception.ResourceNotFoundException;
import com.streaming.movieplatform.repository.EpisodeRepository;
import com.streaming.movieplatform.repository.GenreRepository;
import com.streaming.movieplatform.repository.MovieRepository;
import com.streaming.movieplatform.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/movies")
public class AdminMovieController {

    private final MovieRepository movieRepository;
    private final EpisodeRepository episodeRepository;
    private final GenreRepository genreRepository;
    private final MovieService movieService;

    public AdminMovieController(MovieRepository movieRepository,
                                EpisodeRepository episodeRepository,
                                GenreRepository genreRepository,
                                MovieService movieService) {
        this.movieRepository = movieRepository;
        this.episodeRepository = episodeRepository;
        this.genreRepository = genreRepository;
        this.movieService = movieService;
    }

    @GetMapping
    public String movies(Model model) {
        model.addAttribute("movies", movieRepository.findAll());
        return "admin/movies";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        prepareMovieForm(model, new AdminMovieRequest());
        return "admin/movie-form";
    }

    @GetMapping("/{movieId}/edit")
    public String editForm(@PathVariable Long movieId, Model model) {
        Movie movie = getMovieWithDetailsOrThrow(movieId);
        AdminMovieRequest request = new AdminMovieRequest();
        request.setId(movie.getId());
        request.setTitle(movie.getTitle());
        request.setOriginalTitle(movie.getOriginalTitle());
        request.setShortDescription(movie.getShortDescription());
        request.setDescription(movie.getDescription());
        request.setReleaseYear(movie.getReleaseYear());
        request.setDurationMinutes(movie.getDurationMinutes());
        request.setMovieType(movie.getMovieType());
        request.setAccessLevel(movie.getAccessLevel());
        request.setTrailerUrl(movie.getTrailerUrl());
        request.setPosterUrl(movie.getPosterUrl());
        request.setBackdropUrl(movie.getBackdropUrl());
        request.setFeatured(movie.isFeatured());
        request.setPopular(movie.isPopular());
        request.setActive(movie.isActive());
        request.setGenreIds(movie.getGenres().stream().map(g -> g.getId()).toList());
        request.setCountryName(movie.getCountry() == null ? "" : movie.getCountry().getName());
        request.setActorNames(movie.getActors().stream()
                .map(a -> a.getName())
                .sorted()
                .collect(Collectors.joining(", ")));
        request.setDirectorNames(movie.getDirectors().stream()
                .map(d -> d.getName())
                .sorted()
                .collect(Collectors.joining(", ")));
        prepareMovieForm(model, request);
        return "admin/movie-form";
    }

    @PostMapping("/save")
    public String saveMovie(@Valid @ModelAttribute("movieForm") AdminMovieRequest request,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepareMovieForm(model, request);
            return "admin/movie-form";
        }
        movieService.saveMovie(request);
        redirectAttributes.addFlashAttribute("successMessage", "Lưu phim thành công");
        return "redirect:/admin/movies";
    }

    @PostMapping("/{movieId}/delete")
    public String deleteMovie(@PathVariable Long movieId, RedirectAttributes redirectAttributes) {
        movieService.deleteMovie(movieId);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa phim");
        return "redirect:/admin/movies";
    }

    @GetMapping("/{movieId}/episodes")
    public String episodes(@PathVariable Long movieId, Model model) {
        AdminEpisodeRequest episodeForm = new AdminEpisodeRequest();
        episodeForm.setMovieId(movieId);
        populateEpisodePage(model, getMovieOrThrow(movieId), episodeForm);
        return "admin/episodes";
    }

    @GetMapping("/{movieId}/episodes/{episodeId}/edit")
    public String editEpisode(@PathVariable Long movieId, @PathVariable Long episodeId, Model model) {
        Movie movie = getMovieOrThrow(movieId);
        Episode episode = getEpisodeOrThrow(movieId, episodeId);
        AdminEpisodeRequest episodeForm = new AdminEpisodeRequest();
        episodeForm.setId(episode.getId());
        episodeForm.setMovieId(movieId);
        episodeForm.setEpisodeNumber(episode.getEpisodeNumber());
        episodeForm.setTitle(episode.getTitle());
        episodeForm.setVideoUrl(episode.getVideoUrl());
        episodeForm.setDurationMinutes(episode.getDurationMinutes());
        episodeForm.setFreePreview(episode.isFreePreview());
        episodeForm.setActive(episode.isActive());
        populateEpisodePage(model, movie, episodeForm);
        return "admin/episodes";
    }

    @PostMapping("/episodes/save")
    public String saveEpisode(@Valid @ModelAttribute("episodeForm") AdminEpisodeRequest request,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            Movie movie = getMovieOrThrow(request.getMovieId());
            populateEpisodePage(model, movie, request);
            return "admin/episodes";
        }
        movieService.saveEpisode(request);
        redirectAttributes.addFlashAttribute("successMessage", "Lưu tập phim thành công");
        return "redirect:/admin/movies/" + request.getMovieId() + "/episodes";
    }

    @PostMapping("/{movieId}/episodes/{episodeId}/delete")
    public String deleteEpisode(@PathVariable Long movieId, @PathVariable Long episodeId, RedirectAttributes redirectAttributes) {
        getEpisodeOrThrow(movieId, episodeId);
        movieService.deleteEpisode(episodeId);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa tập phim");
        return "redirect:/admin/movies/" + movieId + "/episodes";
    }

    private void prepareMovieForm(Model model, AdminMovieRequest request) {
        model.addAttribute("movieForm", request);
        model.addAttribute("genres", genreRepository.findAllByOrderByNameAsc());
        model.addAttribute("accessLevels", com.streaming.movieplatform.enums.AccessLevel.values());
        model.addAttribute("movieTypes", com.streaming.movieplatform.enums.MovieType.values());
    }

    private void populateEpisodePage(Model model, Movie movie, AdminEpisodeRequest request) {
        model.addAttribute("movie", movie);
        model.addAttribute("episodes", episodeRepository.findByMovieIdOrderByEpisodeNumberAsc(movie.getId()));
        model.addAttribute("episodeForm", request);
    }

    private Movie getMovieOrThrow(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim"));
    }

    private Movie getMovieWithDetailsOrThrow(Long movieId) {
        return movieRepository.findWithDetailsById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim"));
    }

    private Episode getEpisodeOrThrow(Long movieId, Long episodeId) {
        Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tập phim"));
        if (!episode.getMovie().getId().equals(movieId)) {
            throw new ResourceNotFoundException("Không tìm thấy tập phim thuộc bộ phim này");
        }
        return episode;
    }
}
