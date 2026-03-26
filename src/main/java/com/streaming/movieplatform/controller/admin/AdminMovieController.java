package com.streaming.movieplatform.controller.admin;

import com.streaming.movieplatform.dto.AdminEpisodeRequest;
import com.streaming.movieplatform.dto.AdminMovieRequest;
import com.streaming.movieplatform.entity.Movie;
import com.streaming.movieplatform.repository.ActorRepository;
import com.streaming.movieplatform.repository.CountryRepository;
import com.streaming.movieplatform.repository.DirectorRepository;
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

@Controller
@RequestMapping("/admin/movies")
public class AdminMovieController {

    private final MovieRepository movieRepository;
    private final EpisodeRepository episodeRepository;
    private final CountryRepository countryRepository;
    private final GenreRepository genreRepository;
    private final ActorRepository actorRepository;
    private final DirectorRepository directorRepository;
    private final MovieService movieService;

    public AdminMovieController(MovieRepository movieRepository,
                                EpisodeRepository episodeRepository,
                                CountryRepository countryRepository,
                                GenreRepository genreRepository,
                                ActorRepository actorRepository,
                                DirectorRepository directorRepository,
                                MovieService movieService) {
        this.movieRepository = movieRepository;
        this.episodeRepository = episodeRepository;
        this.countryRepository = countryRepository;
        this.genreRepository = genreRepository;
        this.actorRepository = actorRepository;
        this.directorRepository = directorRepository;
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
        Movie movie = movieRepository.findWithDetailsById(movieId).orElseThrow();
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
        request.setExistingPosterUrl(movie.getPosterUrl());
        request.setExistingBackdropUrl(movie.getBackdropUrl());
        request.setFeatured(movie.isFeatured());
        request.setPopular(movie.isPopular());
        request.setActive(movie.isActive());
        request.setGenreIds(movie.getGenres().stream().map(g -> g.getId()).toList());
        request.setCountryName(movie.getCountry() == null ? "" : movie.getCountry().getName());
        request.setActorNames(movie.getActors().stream()
                .map(a -> a.getName())
                .sorted()
                .collect(java.util.stream.Collectors.joining(", ")));
        request.setDirectorNames(movie.getDirectors().stream()
                .map(d -> d.getName())
                .sorted()
                .collect(java.util.stream.Collectors.joining(", ")));
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
        Movie movie = movieRepository.findById(movieId).orElseThrow();
        model.addAttribute("movie", movie);
        model.addAttribute("episodes", episodeRepository.findByMovieIdOrderByEpisodeNumberAsc(movieId));
        AdminEpisodeRequest episodeForm = new AdminEpisodeRequest();
        episodeForm.setMovieId(movieId);
        model.addAttribute("episodeForm", episodeForm);
        return "admin/episodes";
    }

    @GetMapping("/{movieId}/episodes/{episodeId}/edit")
    public String editEpisode(@PathVariable Long movieId, @PathVariable Long episodeId, Model model) {
        Movie movie = movieRepository.findById(movieId).orElseThrow();
        var episode = episodeRepository.findById(episodeId).orElseThrow();
        AdminEpisodeRequest episodeForm = new AdminEpisodeRequest();
        episodeForm.setId(episode.getId());
        episodeForm.setMovieId(movieId);
        episodeForm.setEpisodeNumber(episode.getEpisodeNumber());
        episodeForm.setTitle(episode.getTitle());
        episodeForm.setVideoUrl(episode.getVideoUrl());
        episodeForm.setDurationMinutes(episode.getDurationMinutes());
        episodeForm.setFreePreview(episode.isFreePreview());
        episodeForm.setActive(episode.isActive());
        model.addAttribute("movie", movie);
        model.addAttribute("episodes", episodeRepository.findByMovieIdOrderByEpisodeNumberAsc(movieId));
        model.addAttribute("episodeForm", episodeForm);
        return "admin/episodes";
    }

    @PostMapping("/episodes/save")
    public String saveEpisode(@Valid @ModelAttribute("episodeForm") AdminEpisodeRequest request,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            Movie movie = movieRepository.findById(request.getMovieId()).orElseThrow();
            model.addAttribute("movie", movie);
            model.addAttribute("episodes", episodeRepository.findByMovieIdOrderByEpisodeNumberAsc(request.getMovieId()));
            return "admin/episodes";
        }
        movieService.saveEpisode(request);
        redirectAttributes.addFlashAttribute("successMessage", "Lưu tập phim thành công");
        return "redirect:/admin/movies/" + request.getMovieId() + "/episodes";
    }

    @PostMapping("/{movieId}/episodes/{episodeId}/delete")
    public String deleteEpisode(@PathVariable Long movieId, @PathVariable Long episodeId, RedirectAttributes redirectAttributes) {
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
}
