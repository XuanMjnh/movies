package com.streaming.movieplatform.dto;

import com.streaming.movieplatform.enums.AccessLevel;
import com.streaming.movieplatform.enums.MovieType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public class AdminMovieRequest {

    private Long id;

    @NotBlank(message = "Tên phim không được để trống")
    @Size(max = 200, message = "Tên phim tối đa 200 ký tự")
    private String title;

    @Size(max = 200, message = "Tên gốc tối đa 200 ký tự")
    private String originalTitle;

    @NotBlank(message = "Mô tả ngắn không được để trống")
    @Size(max = 500, message = "Mô tả ngắn tối đa 500 ký tự")
    private String shortDescription;

    @NotBlank(message = "Mô tả chi tiết không được để trống")
    private String description;

    @NotNull(message = "Năm phát hành không được để trống")
    private Integer releaseYear;

    @NotNull(message = "Thời lượng không được để trống")
    private Integer durationMinutes;

    @NotNull(message = "Loại phim không được để trống")
    private MovieType movieType;

    @NotNull(message = "Quyền truy cập không được để trống")
    private AccessLevel accessLevel;

    @NotBlank(message = "Quốc gia không được để trống")
    @Size(max = 120, message = "Quốc gia tối đa 120 ký tự")
    private String countryName;

    private String trailerUrl;
    private String existingPosterUrl;
    private String existingBackdropUrl;
    private MultipartFile posterFile;
    private MultipartFile backdropFile;
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


    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }

    public String getExistingPosterUrl() {
        return existingPosterUrl;
    }

    public void setExistingPosterUrl(String existingPosterUrl) {
        this.existingPosterUrl = existingPosterUrl;
    }

    public String getExistingBackdropUrl() {
        return existingBackdropUrl;
    }

    public void setExistingBackdropUrl(String existingBackdropUrl) {
        this.existingBackdropUrl = existingBackdropUrl;
    }

    public MultipartFile getPosterFile() {
        return posterFile;
    }

    public void setPosterFile(MultipartFile posterFile) {
        this.posterFile = posterFile;
    }

    public MultipartFile getBackdropFile() {
        return backdropFile;
    }

    public void setBackdropFile(MultipartFile backdropFile) {
        this.backdropFile = backdropFile;
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
