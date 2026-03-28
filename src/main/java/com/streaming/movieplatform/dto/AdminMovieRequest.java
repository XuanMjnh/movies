package com.streaming.movieplatform.dto;

import com.streaming.movieplatform.enums.AccessLevel;
import com.streaming.movieplatform.enums.MovieType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class AdminMovieRequest {

    private Long id;

    @NotBlank(message = "Ten phim khong duoc de trong")
    @Size(max = 200, message = "Ten phim toi da 200 ky tu")
    private String title;

    @Size(max = 200, message = "Ten goc toi da 200 ky tu")
    private String originalTitle;

    @NotBlank(message = "Mo ta ngan khong duoc de trong")
    @Size(max = 500, message = "Mo ta ngan toi da 500 ky tu")
    private String shortDescription;

    @NotBlank(message = "Mo ta chi tiet khong duoc de trong")
    private String description;

    @NotNull(message = "Nam phat hanh khong duoc de trong")
    private Integer releaseYear;

    @NotNull(message = "Thoi luong khong duoc de trong")
    private Integer durationMinutes;

    @NotNull(message = "Loai phim khong duoc de trong")
    private MovieType movieType;

    @NotNull(message = "Quyen truy cap khong duoc de trong")
    private AccessLevel accessLevel;

    @NotBlank(message = "Quoc gia khong duoc de trong")
    @Size(max = 120, message = "Quoc gia toi da 120 ky tu")
    private String countryName;

    @Size(max = 255, message = "Poster URL toi da 255 ky tu")
    private String posterUrl;

    @Size(max = 255, message = "Backdrop URL toi da 255 ky tu")
    private String backdropUrl;

    private String trailerUrl;
    private boolean featured;
    private boolean popular;
    private boolean active = true;
    private List<Long> genreIds = new ArrayList<>();
    private String actorNames;
    private String directorNames;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public MovieType getMovieType() {
        return movieType;
    }

    public void setMovieType(MovieType movieType) {
        this.movieType = movieType;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getBackdropUrl() {
        return backdropUrl;
    }

    public void setBackdropUrl(String backdropUrl) {
        this.backdropUrl = backdropUrl;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public boolean isPopular() {
        return popular;
    }

    public void setPopular(boolean popular) {
        this.popular = popular;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Long> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Long> genreIds) {
        this.genreIds = genreIds;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getActorNames() {
        return actorNames;
    }

    public void setActorNames(String actorNames) {
        this.actorNames = actorNames;
    }

    public String getDirectorNames() {
        return directorNames;
    }

    public void setDirectorNames(String directorNames) {
        this.directorNames = directorNames;
    }
}
