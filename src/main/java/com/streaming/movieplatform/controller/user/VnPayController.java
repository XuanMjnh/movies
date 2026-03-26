package com.streaming.movieplatform.controller.user;

import com.streaming.movieplatform.dto.VnPayIpnResponse;
import com.streaming.movieplatform.service.WalletService;
import com.streaming.movieplatform.util.VnPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/payment/vnpay")
public class VnPayController {

    private final WalletService walletService;

    public VnPayController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/return")
    public String paymentReturn(HttpServletRequest request, Model model) {
        model.addAttribute("result", walletService.getVnPayReturnResult(VnPayUtil.extractVnpParams(request.getParameterMap())));
        return "wallet/vnpay-return";
    }

    @GetMapping(value = "/ipn", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VnPayIpnResponse> ipn(HttpServletRequest request) {
        return ResponseEntity.ok(walletService.handleVnPayIpn(VnPayUtil.extractVnpParams(request.getParameterMap())));
    }
}
