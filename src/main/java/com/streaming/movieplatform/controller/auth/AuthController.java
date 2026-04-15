package com.streaming.movieplatform.controller.auth;

import com.streaming.movieplatform.controller.support.FormFlowSupport;
import com.streaming.movieplatform.dto.RegisterRequest;
import com.streaming.movieplatform.exception.BusinessException;
import com.streaming.movieplatform.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        FormFlowSupport.addIfAbsent(model, "registerRequest", RegisterRequest::new);
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return FormFlowSupport.redirectWithValidationErrors(redirectAttributes, "/register", "registerRequest", request, bindingResult);
        }
        try {
            userService.register(request);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký thành công. Bạn có thể đăng nhập ngay bây giờ.");
            return "redirect:/login";
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            FormFlowSupport.flashForm(redirectAttributes, "registerRequest", request);
            return "redirect:/register";
        }
    }
}
