package com.streaming.movieplatform.controller.user;

import com.streaming.movieplatform.controller.support.FormFlowSupport;
import com.streaming.movieplatform.dto.DepositRequest;
import com.streaming.movieplatform.exception.BusinessException;
import com.streaming.movieplatform.service.WalletService;
import com.streaming.movieplatform.service.UserService;
import com.streaming.movieplatform.util.VnPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user/wallet")
public class WalletController {

    private final WalletService walletService;
    private final UserService userService;

    public WalletController(WalletService walletService, UserService userService) {
        this.walletService = walletService;
        this.userService = userService;
    }

    @GetMapping
    public String wallet(Model model) {
        model.addAttribute("wallet", walletService.getWallet(userService.getCurrentUser()));
        model.addAttribute("recentTransactions", walletService.getTransactions(userService.getCurrentUser()));
        return "wallet/index";
    }

    @GetMapping("/deposit")
    public String depositPage(Model model) {
        FormFlowSupport.addIfAbsent(model, "depositRequest", DepositRequest::new);
        model.addAttribute("wallet", walletService.getWallet(userService.getCurrentUser()));
        return "wallet/deposit";
    }

    @PostMapping("/deposit")
    public String deposit(@Valid @ModelAttribute("depositRequest") DepositRequest request,
                          BindingResult bindingResult,
                          HttpServletRequest httpServletRequest,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return FormFlowSupport.redirectWithValidationErrors(redirectAttributes, "/user/wallet/deposit", "depositRequest", request, bindingResult);
        }
        try {
            String paymentUrl = walletService.createVnPayDepositPaymentUrl(
                    userService.getCurrentUser(),
                    request,
                    VnPayUtil.getClientIp(httpServletRequest)
            );
            return "redirect:" + paymentUrl;
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            FormFlowSupport.flashForm(redirectAttributes, "depositRequest", request);
            return "redirect:/user/wallet/deposit";
        }
    }

    @GetMapping("/transactions")
    public String transactions(Model model) {
        model.addAttribute("transactions", walletService.getTransactions(userService.getCurrentUser()));
        return "wallet/transactions";
    }
}
