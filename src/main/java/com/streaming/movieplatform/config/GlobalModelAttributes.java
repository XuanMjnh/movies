package com.streaming.movieplatform.config;

import com.streaming.movieplatform.entity.User;
import com.streaming.movieplatform.repository.CountryRepository;
import com.streaming.movieplatform.service.UserService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.Year;

@ControllerAdvice
public class GlobalModelAttributes {

    private final UserService userService;
    private final CountryRepository countryRepository;

    public GlobalModelAttributes(UserService userService, CountryRepository countryRepository) {
        this.userService = userService;
        this.countryRepository = countryRepository;
    }

    @ModelAttribute("loggedInUser")
    public User loggedInUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("currentAccessLevel")
    public String currentAccessLevel() {
        User user = userService.getCurrentUser();
        return user == null ? "FREE" : userService.getCurrentAccessLevel(user).name();
    }

    @ModelAttribute("currentYear")
    public int currentYear() {
        return Year.now().getValue();
    }

    @ModelAttribute("southKoreaCountryId")
    public Long southKoreaCountryId() {
        return resolveCountryId("Hàn Quốc", "South Korea", "Korea");
    }

    @ModelAttribute("usaCountryId")
    public Long usaCountryId() {
        return resolveCountryId("Mỹ", "USA", "United States", "United States of America");
    }

    private Long resolveCountryId(String... candidates) {
        for (String candidate : candidates) {
            var country = countryRepository.findByNameIgnoreCase(candidate);
            if (country.isPresent()) {
                return country.get().getId();
            }
        }
        return null;
    }
}
