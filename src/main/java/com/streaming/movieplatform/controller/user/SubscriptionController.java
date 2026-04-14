package com.streaming.movieplatform.controller.user;

import com.streaming.movieplatform.entity.User;
import com.streaming.movieplatform.exception.BusinessException;
import com.streaming.movieplatform.service.SubscriptionService;
import com.streaming.movieplatform.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final UserService userService;

    public SubscriptionController(SubscriptionService subscriptionService, UserService userService) {
        this.subscriptionService = subscriptionService;
        this.userService = userService;
    }

    @GetMapping("/subscription/plans")
    public String pricing(Model model) {
        var user = userService.getCurrentUser();
        if (user != null) {
            subscriptionService.refreshStatus(user);
        }
        populatePricingModel(model, user);
        return "subscription/pricing";
    }

    @GetMapping("/user/subscription/current")
    public String currentSubscription(Model model) {
        var user = userService.getCurrentUser();
        subscriptionService.refreshStatus(user);
        populateCurrentSubscriptionModel(model, user);
        return "subscription/current";
    }

    @PostMapping("/user/subscription/purchase/{planId}")
    public String purchase(@PathVariable Long planId,
                           @RequestParam(required = false) String voucherCode,
                           RedirectAttributes redirectAttributes) {
        try {
            subscriptionService.purchasePlan(userService.getCurrentUser(), planId, voucherCode);
            redirectAttributes.addFlashAttribute("successMessage", "Mua gói thành viên thành công");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/user/subscription/current";
    }

    private void populatePricingModel(Model model, User user) {
        model.addAttribute("plans", subscriptionService.getActivePlans());
        model.addAttribute("eligibleVouchers", user == null ? List.of() : subscriptionService.getEligibleDisplayVouchers(user));
        model.addAttribute("currentSubscription", user == null ? null : subscriptionService.getCurrentSubscription(user));
        model.addAttribute("walletBalance", user == null || user.getWallet() == null ? BigDecimal.ZERO : user.getWallet().getBalance());
    }

    private void populateCurrentSubscriptionModel(Model model, User user) {
        model.addAttribute("currentSubscription", subscriptionService.getCurrentSubscription(user));
        model.addAttribute("subscriptionHistory", subscriptionService.getSubscriptionHistory(user));
        model.addAttribute("remainingDays", userService.getRemainingSubscriptionDays(user));
    }
}
