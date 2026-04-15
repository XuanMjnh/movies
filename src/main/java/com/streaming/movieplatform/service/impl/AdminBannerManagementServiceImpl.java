package com.streaming.movieplatform.service.impl;

import com.streaming.movieplatform.dto.AdminBannerRequest;
import com.streaming.movieplatform.entity.Banner;
import com.streaming.movieplatform.entity.Movie;
import com.streaming.movieplatform.exception.ResourceNotFoundException;
import com.streaming.movieplatform.repository.BannerRepository;
import com.streaming.movieplatform.repository.MovieRepository;
import com.streaming.movieplatform.service.AdminBannerManagementService;
import com.streaming.movieplatform.service.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminBannerManagementServiceImpl implements AdminBannerManagementService {

    private final BannerRepository bannerRepository;
    private final MovieRepository movieRepository;
    private final StorageService storageService;

    public AdminBannerManagementServiceImpl(BannerRepository bannerRepository,
                                            MovieRepository movieRepository,
                                            StorageService storageService) {
        this.bannerRepository = bannerRepository;
        this.movieRepository = movieRepository;
        this.storageService = storageService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Banner> getAllBanners() {
        return bannerRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movie> getAvailableMovies() {
        return movieRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Banner getBannerById(Long bannerId) {
        return bannerRepository.findById(bannerId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay banner"));
    }

    @Override
    public Banner saveBanner(AdminBannerRequest request) {
        Banner banner = request.getId() == null ? new Banner() : getBannerById(request.getId());
        banner.setTitle(request.getTitle());
        banner.setSubtitle(request.getSubtitle());
        banner.setImageUrl(storageService.store(request.getImageFile(), "banners", request.getExistingImageUrl()));
        banner.setCtaText(request.getCtaText());
        banner.setCtaLink(request.getCtaLink());
        banner.setDisplayOrder(request.getDisplayOrder());
        banner.setActive(request.isActive());
        banner.setMovie(resolveMovie(request.getMovieId()));
        return bannerRepository.save(banner);
    }

    @Override
    public void deleteBanner(Long bannerId) {
        bannerRepository.deleteById(bannerId);
    }

    private Movie resolveMovie(Long movieId) {
        if (movieId == null) {
            return null;
        }
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay phim lien ket banner"));
    }
}
