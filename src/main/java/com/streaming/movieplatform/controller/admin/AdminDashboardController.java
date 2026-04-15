package com.streaming.movieplatform.controller.admin;

import com.streaming.movieplatform.service.AdminDashboardService;
import com.streaming.movieplatform.service.SubscriptionService;
import com.streaming.movieplatform.service.WalletService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;
    private final WalletService walletService;
    private final SubscriptionService subscriptionService;

    public AdminDashboardController(AdminDashboardService adminDashboardService,
                                    WalletService walletService,
                                    SubscriptionService subscriptionService) {
        this.adminDashboardService = adminDashboardService;
        this.walletService = walletService;
        this.subscriptionService = subscriptionService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("stats", adminDashboardService.getDashboardStats());
        model.addAttribute("dailyRevenue", adminDashboardService.getDailyRevenueStats());
        model.addAttribute("monthlyRevenue", adminDashboardService.getMonthlyRevenueStats());
        model.addAttribute("movieTypeStats", adminDashboardService.getMovieTypeStats());
        model.addAttribute("topViewedMovies", adminDashboardService.getTopViewedMovieStats());
        model.addAttribute("latestTransactions", walletService.getLatestTransactions());
        model.addAttribute("latestSubscriptions", subscriptionService.getLatestSubscriptions());
        return "admin/dashboard";
    }
}
