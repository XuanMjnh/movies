package com.streaming.movieplatform.controller.admin;

import com.streaming.movieplatform.service.SubscriptionService;
import com.streaming.movieplatform.service.WalletService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/transactions")
public class AdminTransactionManagementController {

    private final WalletService walletService;
    private final SubscriptionService subscriptionService;

    public AdminTransactionManagementController(WalletService walletService,
                                                SubscriptionService subscriptionService) {
        this.walletService = walletService;
        this.subscriptionService = subscriptionService;
    }

    @GetMapping
    public String transactions(Model model) {
        model.addAttribute("walletTransactions", walletService.getLatestTransactions());
        model.addAttribute("paymentTransactions", walletService.getPaymentTransactions());
        model.addAttribute("subscriptionHistory", subscriptionService.getLatestSubscriptions());
        return "admin/transactions";
    }
}
