package com.streaming.movieplatform.controller;

import com.streaming.movieplatform.entity.User;
import com.streaming.movieplatform.repository.BannerRepository;
import com.streaming.movieplatform.repository.GenreRepository;
import com.streaming.movieplatform.service.MovieService;
import com.streaming.movieplatform.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final MovieService movieService;
    private final UserService userService;
    private final BannerRepository bannerRepository;
    private final GenreRepository genreRepository;

    public HomeController(MovieService movieService,
                          UserService userService,
                          BannerRepository bannerRepository,
                          GenreRepository genreRepository) {
        this.movieService = movieService;
        this.userService = userService;
        this.bannerRepository = bannerRepository;
        this.genreRepository = genreRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("sections", movieService.getHomeSections(currentUser));
        model.addAttribute("banners", bannerRepository.findByActiveTrueOrderByDisplayOrderAsc());
        model.addAttribute("genres", genreRepository.findAllByOrderByNameAsc());
        return "home/index";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "auth/access-denied";
    }
}
