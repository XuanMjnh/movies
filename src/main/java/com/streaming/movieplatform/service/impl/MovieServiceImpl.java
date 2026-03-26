package com.streaming.movieplatform.service.impl;

import com.streaming.movieplatform.dto.AdminEpisodeRequest;
import com.streaming.movieplatform.dto.AdminMovieRequest;
import com.streaming.movieplatform.dto.MovieFilterRequest;
import com.streaming.movieplatform.entity.Actor;
import com.streaming.movieplatform.entity.Country;
import com.streaming.movieplatform.entity.Director;
import com.streaming.movieplatform.entity.Episode;
import com.streaming.movieplatform.entity.Favorite;
import com.streaming.movieplatform.entity.Genre;
import com.streaming.movieplatform.entity.Movie;
import com.streaming.movieplatform.entity.Rating;
import com.streaming.movieplatform.entity.User;
import com.streaming.movieplatform.entity.WatchHistory;
import com.streaming.movieplatform.exception.BusinessException;
import com.streaming.movieplatform.exception.ResourceNotFoundException;
import com.streaming.movieplatform.repository.ActorRepository;
import com.streaming.movieplatform.repository.CountryRepository;
import com.streaming.movieplatform.repository.DirectorRepository;
import com.streaming.movieplatform.repository.EpisodeRepository;
import com.streaming.movieplatform.repository.FavoriteRepository;
import com.streaming.movieplatform.repository.GenreRepository;
import com.streaming.movieplatform.repository.MovieRepository;
import com.streaming.movieplatform.repository.RatingRepository;
import com.streaming.movieplatform.repository.WatchHistoryRepository;
import com.streaming.movieplatform.service.MovieService;
import com.streaming.movieplatform.service.StorageService;
import com.streaming.movieplatform.service.UserService;
import com.streaming.movieplatform.util.SlugUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@Service
@Transactional
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final EpisodeRepository episodeRepository;
    private final GenreRepository genreRepository;
    private final CountryRepository countryRepository;
    private final ActorRepository actorRepository;
    private final DirectorRepository directorRepository;
    private final FavoriteRepository favoriteRepository;
    private final WatchHistoryRepository watchHistoryRepository;
    private final RatingRepository ratingRepository;
    private final UserService userService;
    private final StorageService storageService;

    public MovieServiceImpl(MovieRepository movieRepository,
                            EpisodeRepository episodeRepository,
                            GenreRepository genreRepository,
                            CountryRepository countryRepository,
                            ActorRepository actorRepository,
                            DirectorRepository directorRepository,
                            FavoriteRepository favoriteRepository,
                            WatchHistoryRepository watchHistoryRepository,
                            RatingRepository ratingRepository,
                            UserService userService,
                            StorageService storageService) {
        this.movieRepository = movieRepository;
        this.episodeRepository = episodeRepository;
        this.genreRepository = genreRepository;
        this.countryRepository = countryRepository;
        this.actorRepository = actorRepository;
        this.directorRepository = directorRepository;
        this.favoriteRepository = favoriteRepository;
        this.watchHistoryRepository = watchHistoryRepository;
        this.ratingRepository = ratingRepository;
        this.userService = userService;
        this.storageService = storageService;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<Movie>> getHomeSections(User currentUser) {
        List<Movie> continueWatching = currentUser == null ? List.of() : getWatchHistory(currentUser).stream()
                .map(WatchHistory::getMovie)
                .distinct()
                .limit(8)
                .toList();

        return Map.of(
                "featured", movieRepository.findTop8ByFeaturedTrueAndActiveTrueOrderByCreatedAtDesc(),
                "latest", movieRepository.findTop12ByActiveTrueOrderByCreatedAtDesc(),
                "popular", movieRepository.findTop12ByActiveTrueOrderByViewCountDesc(),
                "premium", movieRepository.findTop12ByAccessLevelAndActiveTrueOrderByCreatedAtDesc(com.streaming.movieplatform.enums.AccessLevel.PREMIUM),
                "continueWatching", continueWatching
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Movie> searchMovies(MovieFilterRequest filter) {
        Sort sort = switch (filter.getSortBy() == null ? "newest" : filter.getSortBy()) {
            case "popular" -> Sort.by(Sort.Direction.DESC, "viewCount");
            case "rating" -> Sort.by(Sort.Direction.DESC, "averageRating");
            case "title" -> Sort.by(Sort.Direction.ASC, "title");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
        Pageable pageable = PageRequest.of(Math.max(filter.getPage(), 0), 12, sort);
        String keyword = filter.getKeyword();
        if (keyword != null && keyword.isBlank()) {
            keyword = null;
        }
        return movieRepository.searchMovies(keyword, filter.getGenreId(), filter.getCountryId(), filter.getReleaseYear(),
                filter.getAccessLevel(), filter.getMovieType(), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Movie getMovieDetails(Long movieId) {
        return movieRepository.findWithDetailsById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim"));
    }

    @Override
    @Transactional(readOnly = true)
    public Episode resolveEpisode(Long movieId, Long episodeId) {
        if (episodeId != null) {
            Episode episode = episodeRepository.findById(episodeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tập phim"));
            if (!episode.getMovie().getId().equals(movieId)) {
                throw new BusinessException("Tập phim không thuộc bộ phim đã chọn");
            }
            return episode;
        }
        return episodeRepository.findFirstByMovieIdAndActiveTrueOrderByEpisodeNumberAsc(movieId)
                .orElseThrow(() -> new BusinessException("Phim chưa có tập phát hành"));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canWatch(User user, Movie movie) {
        if (movie.getAccessLevel() == com.streaming.movieplatform.enums.AccessLevel.FREE) {
            return true;
        }
        if (user == null) {
            return false;
        }
        return userService.getCurrentAccessLevel(user).allows(movie.getAccessLevel());
    }

    @Override
    public void saveWatchProgress(User user, Long episodeId, Integer positionSeconds, boolean completed) {
        if (user == null) {
            return;
        }
        Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tập phim"));
        Movie movie = episode.getMovie();
        if (!canWatch(user, movie)) {
            throw new BusinessException("Gói hiện tại không đủ quyền để xem phim này");
        }

        WatchHistory history = watchHistoryRepository.findByUserIdAndEpisodeId(user.getId(), episodeId).orElse(null);
        boolean isNewHistory = history == null;
        if (history == null) {
            history = new WatchHistory();
            history.setUser(user);
            history.setMovie(movie);
            history.setEpisode(episode);
        }
        history.setLastPositionSeconds(positionSeconds == null ? 0 : Math.max(positionSeconds, 0));
        history.setCompleted(completed);
        history.setLastWatchedAt(LocalDateTime.now());
        watchHistoryRepository.save(history);

        if (isNewHistory) {
            movie.setViewCount(movie.getViewCount() + 1);
            movieRepository.save(movie);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movie> getRelatedMovies(Movie movie) {
        if (movie.getGenres() == null || movie.getGenres().isEmpty()) {
            return movieRepository.findTop12ByActiveTrueOrderByCreatedAtDesc().stream()
                    .filter(item -> !item.getId().equals(movie.getId()))
                    .limit(6)
                    .toList();
        }
        return movieRepository.findRelatedMovies(movie.getId(), movie.getCountry(), movie.getGenres(), PageRequest.of(0, 6));
    }

    @Override
    public boolean toggleFavorite(User user, Long movieId) {
        boolean exists = favoriteRepository.existsByUserIdAndMovieId(user.getId(), movieId);
        if (exists) {
            favoriteRepository.deleteByUserIdAndMovieId(user.getId(), movieId);
            return false;
        }
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim"));
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setMovie(movie);
        favoriteRepository.save(favorite);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFavorite(User user, Long movieId) {
        if (user == null) {
            return false;
        }
        return favoriteRepository.existsByUserIdAndMovieId(user.getId(), movieId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Favorite> getFavoriteMovies(User user) {
        return favoriteRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WatchHistory> getWatchHistory(User user) {
        return watchHistoryRepository.findByUserIdOrderByLastWatchedAtDesc(user.getId());
    }

    @Override
    public void deleteHistoryItem(User user, Long historyId) {
        watchHistoryRepository.deleteByUserIdAndId(user.getId(), historyId);
    }

    @Override
    public void clearWatchHistory(User user) {
        watchHistoryRepository.deleteAll(getWatchHistory(user));
    }

    @Override
    public void submitRating(User user, Long movieId, Integer stars) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim"));
        Rating rating = ratingRepository.findByUserIdAndMovieId(user.getId(), movieId).orElse(null);
        if (rating == null) {
            rating = new Rating();
            rating.setMovie(movie);
            rating.setUser(user);
        }
        rating.setStars(stars);
        ratingRepository.save(rating);

        List<Rating> ratings = ratingRepository.findByMovieId(movieId);
        double average = ratings.stream().mapToInt(Rating::getStars).average().orElse(0.0);
        movie.setAverageRating(Math.round(average * 10.0) / 10.0);
        movieRepository.save(movie);
    }

    @Override
    public Movie saveMovie(AdminMovieRequest request) {
        Movie movie = request.getId() == null ? new Movie() : movieRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim để cập nhật"));

        Country country = getOrCreateCountry(request.getCountryName());

        movie.setTitle(request.getTitle().trim());
        movie.setOriginalTitle(request.getOriginalTitle());
        movie.setShortDescription(request.getShortDescription());
        movie.setDescription(request.getDescription());
        movie.setReleaseYear(request.getReleaseYear());
        movie.setDurationMinutes(request.getDurationMinutes());
        movie.setMovieType(request.getMovieType());
        movie.setAccessLevel(request.getAccessLevel());
        movie.setCountry(country);
        movie.setTrailerUrl(request.getTrailerUrl());
        movie.setFeatured(request.isFeatured());
        movie.setPopular(request.isPopular());
        movie.setActive(request.isActive());
        movie.setPosterUrl(storageService.store(request.getPosterFile(), "posters", request.getExistingPosterUrl()));
        movie.setBackdropUrl(storageService.store(request.getBackdropFile(), "backdrops", request.getExistingBackdropUrl()));

        Set<Genre> genres = new HashSet<>(
        genreRepository.findAllById(request.getGenreIds() == null ? List.of() : request.getGenreIds())
);

        Set<Actor> actors = parseNames(request.getActorNames()).stream()
                .map(this::getOrCreateActor)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<Director> directors = parseNames(request.getDirectorNames()).stream()
                .map(this::getOrCreateDirector)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        movie.setGenres(genres);
        movie.setActors(actors);
        movie.setDirectors(directors);

        String baseSlug = SlugUtil.toSlug(request.getTitle());

        if (movie.getId() == null) {
            movie.setSlug(baseSlug + "-" + System.currentTimeMillis());
            Movie savedMovie = movieRepository.save(movie);
            savedMovie.setSlug(baseSlug + "-" + savedMovie.getId());
            return movieRepository.save(savedMovie);
        } else {
            movie.setSlug(baseSlug + "-" + movie.getId());
            return movieRepository.save(movie);
        }
    }

    @Override
    public Episode saveEpisode(AdminEpisodeRequest request) {
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim"));

        Episode episode = request.getId() == null ? new Episode() : episodeRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tập phim"));
        episode.setMovie(movie);
        episode.setEpisodeNumber(request.getEpisodeNumber());
        episode.setTitle(request.getTitle());
        episode.setVideoUrl(request.getVideoUrl());
        episode.setDurationMinutes(request.getDurationMinutes());
        episode.setFreePreview(request.isFreePreview());
        episode.setActive(request.isActive());
        return episodeRepository.save(episode);
    }

    @Override
    public void deleteMovie(Long movieId) {
        movieRepository.deleteById(movieId);
    }

    @Override
    public void deleteEpisode(Long episodeId) {
        episodeRepository.deleteById(episodeId);
    }

    private Country getOrCreateCountry(String countryName) {
        String normalizedName = normalizeName(countryName);
        return countryRepository.findByNameIgnoreCase(normalizedName)
                .orElseGet(() -> {
                    Country country = new Country();
                    country.setName(normalizedName);
                    return countryRepository.save(country);
                });
    }

    private Actor getOrCreateActor(String actorName) {
        String normalizedName = normalizeName(actorName);
        return actorRepository.findByNameIgnoreCase(normalizedName)
                .orElseGet(() -> {
                    Actor actor = new Actor();
                    actor.setName(normalizedName);
                    return actorRepository.save(actor);
                });
    }

    private Director getOrCreateDirector(String directorName) {
        String normalizedName = normalizeName(directorName);
        return directorRepository.findByNameIgnoreCase(normalizedName)
                .orElseGet(() -> {
                    Director director = new Director();
                    director.setName(normalizedName);
                    return directorRepository.save(director);
                });
    }

    private LinkedHashSet<String> parseNames(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return new LinkedHashSet<>();
        }

        return Arrays.stream(rawValue.split(","))
                .map(this::normalizeName)
                .filter(name -> !name.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private String normalizeName(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ");
    }
}
