package com.streaming.movieplatform.service;

import com.streaming.movieplatform.dto.AdminBannerRequest;
import com.streaming.movieplatform.entity.Banner;
import com.streaming.movieplatform.entity.Movie;

import java.util.List;

public interface AdminBannerManagementService {
    List<Banner> getAllBanners();
    List<Movie> getAvailableMovies();
    Banner getBannerById(Long bannerId);
    Banner saveBanner(AdminBannerRequest request);
    void deleteBanner(Long bannerId);
}
