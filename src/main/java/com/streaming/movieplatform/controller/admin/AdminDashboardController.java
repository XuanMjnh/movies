package com.streaming.movieplatform.controller.admin;

import com.streaming.movieplatform.service.AdminService;
import com.streaming.movieplatform.service.SubscriptionService;
import com.streaming.movieplatform.service.WalletService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final AdminService adminService;
    private final WalletService walletService;
    private final SubscriptionService subscriptionService;

    public AdminDashboardController(AdminService adminService,
                                    WalletService walletService,
                                    SubscriptionService subscriptionService) {
        this.adminService = adminService;
        this.walletService = walletService;
        this.subscriptionService = subscriptionService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("stats", adminService.getDashboardStats());
        model.addAttribute("dailyRevenue", adminService.getDailyRevenueStats());
        model.addAttribute("monthlyRevenue", adminService.getMonthlyRevenueStats());
        model.addAttribute("movieTypeStats", adminService.getMovieTypeStats());
        model.addAttribute("topViewedMovies", adminService.getTopViewedMovieStats());
        model.addAttribute("latestTransactions", walletService.getLatestTransactions());
        model.addAttribute("latestSubscriptions", subscriptionService.getLatestSubscriptions());
        return "admin/dashboard";
    }
}
