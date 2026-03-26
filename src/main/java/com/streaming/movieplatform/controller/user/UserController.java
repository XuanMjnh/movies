package com.streaming.movieplatform.controller.user;

import com.streaming.movieplatform.dto.CommentRequest;
import com.streaming.movieplatform.dto.PasswordChangeRequest;
import com.streaming.movieplatform.dto.ProfileUpdateRequest;
import com.streaming.movieplatform.dto.RatingRequest;
import com.streaming.movieplatform.dto.WatchProgressRequest;
import com.streaming.movieplatform.exception.BusinessException;
import com.streaming.movieplatform.service.CommentService;
import com.streaming.movieplatform.service.MovieService;
import com.streaming.movieplatform.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final MovieService movieService;
    private final CommentService commentService;

    public UserController(UserService userService,
                          MovieService movieService,
                          CommentService commentService) {
        this.userService = userService;
        this.movieService = movieService;
        this.commentService = commentService;
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        var user = userService.getCurrentUser();
        if (!model.containsAttribute("profileUpdateRequest")) {
            ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();
            profileUpdateRequest.setFullName(user.getFullName());
            profileUpdateRequest.setEmail(user.getEmail());
            profileUpdateRequest.setPhone(user.getPhone());
            model.addAttribute("profileUpdateRequest", profileUpdateRequest);
        }
        if (!model.containsAttribute("passwordChangeRequest")) {
            model.addAttribute("passwordChangeRequest", new PasswordChangeRequest());
        }
        model.addAttribute("activeSubscription", userService.getCurrentSubscription(user));
        model.addAttribute("remainingDays", userService.getRemainingSubscriptionDays(user));
        return "user/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("profileUpdateRequest") ProfileUpdateRequest request,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.profileUpdateRequest", bindingResult);
            redirectAttributes.addFlashAttribute("profileUpdateRequest", request);
            return "redirect:/user/profile";
        }
        try {
            userService.updateProfile(request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hồ sơ thành công");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/user/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute("passwordChangeRequest") PasswordChangeRequest request,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.passwordChangeRequest", bindingResult);
            redirectAttributes.addFlashAttribute("passwordChangeRequest", request);
            return "redirect:/user/profile";
        }
        try {
            userService.changePassword(request);
            redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/user/profile";
    }

    @GetMapping("/favorites")
    public String favorites(Model model) {
        model.addAttribute("favorites", movieService.getFavoriteMovies(userService.getCurrentUser()));
        return "user/favorites";
    }

    @PostMapping("/favorites/toggle/{movieId}")
    public String toggleFavorite(@PathVariable Long movieId, RedirectAttributes redirectAttributes) {
        boolean added = movieService.toggleFavorite(userService.getCurrentUser(), movieId);
        redirectAttributes.addFlashAttribute("successMessage", added ? "Đã thêm vào yêu thích" : "Đã xóa khỏi yêu thích");
        return "redirect:/movies/" + movieId;
    }

    @GetMapping("/history")
    public String history(Model model) {
        model.addAttribute("histories", movieService.getWatchHistory(userService.getCurrentUser()));
        return "user/history";
    }

    @PostMapping("/history/delete/{historyId}")
    public String deleteHistory(@PathVariable Long historyId, RedirectAttributes redirectAttributes) {
        movieService.deleteHistoryItem(userService.getCurrentUser(), historyId);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa mục lịch sử xem");
        return "redirect:/user/history";
    }

    @PostMapping("/history/clear")
    public String clearHistory(RedirectAttributes redirectAttributes) {
        movieService.clearWatchHistory(userService.getCurrentUser());
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa toàn bộ lịch sử xem");
        return "redirect:/user/history";
    }

    @PostMapping("/history/progress")
    public ResponseEntity<?> saveProgress(@RequestBody WatchProgressRequest request) {
        if (request.getEpisodeId() == null || request.getPositionSeconds() == null || request.getPositionSeconds() < 0) {
            return ResponseEntity.badRequest().body(Map.of("message", "Dữ liệu lưu tiến độ không hợp lệ"));
        }
        movieService.saveWatchProgress(userService.getCurrentUser(), request.getEpisodeId(), request.getPositionSeconds(), Boolean.TRUE.equals(request.getCompleted()));
        return ResponseEntity.ok(Map.of("message", "Đã lưu tiến độ xem"));
    }

    @PostMapping("/movies/{movieId}/comment")
    public String addComment(@PathVariable Long movieId,
                             @Valid @ModelAttribute("commentRequest") CommentRequest request,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nội dung bình luận chưa hợp lệ");
            return "redirect:/movies/" + movieId;
        }
        commentService.addComment(userService.getCurrentUser(), movieId, request.getContent());
        redirectAttributes.addFlashAttribute("successMessage", "Đã đăng bình luận thành công");
        return "redirect:/movies/" + movieId;
    }

    @PostMapping("/movies/{movieId}/rating")
    public String rateMovie(@PathVariable Long movieId,
                            @Valid @ModelAttribute("ratingRequest") RatingRequest request,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Rating chưa hợp lệ");
            return "redirect:/movies/" + movieId;
        }
        movieService.submitRating(userService.getCurrentUser(), movieId, request.getStars());
        redirectAttributes.addFlashAttribute("successMessage", "Đã gửi đánh giá sao cho phim");
        return "redirect:/movies/" + movieId;
    }
}
