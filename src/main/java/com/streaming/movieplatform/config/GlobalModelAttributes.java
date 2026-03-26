package com.streaming.movieplatform.config;

import com.streaming.movieplatform.entity.User;
import com.streaming.movieplatform.service.UserService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.Year;

@ControllerAdvice
public class GlobalModelAttributes {

    private final UserService userService;

    public GlobalModelAttributes(UserService userService) {
        this.userService = userService;
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
}
