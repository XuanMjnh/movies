package com.streaming.movieplatform.service;

import com.streaming.movieplatform.dto.DepositRequest;
import com.streaming.movieplatform.dto.VnPayIpnResponse;
import com.streaming.movieplatform.dto.VnPayReturnResult;
import com.streaming.movieplatform.entity.PaymentTransaction;
import com.streaming.movieplatform.entity.User;
import com.streaming.movieplatform.entity.Wallet;
import com.streaming.movieplatform.entity.WalletTransaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface WalletService {
    Wallet getWallet(User user);
    WalletTransaction deposit(User user, DepositRequest request);
    String createVnPayDepositPaymentUrl(User user, DepositRequest request, String clientIp);
    VnPayIpnResponse handleVnPayIpn(Map<String, String> params);
    VnPayReturnResult getVnPayReturnResult(Map<String, String> params);
    List<WalletTransaction> getTransactions(User user);
    List<WalletTransaction> getLatestTransactions();
    List<PaymentTransaction> getPaymentTransactions();
    WalletTransaction adjustBalanceByAdmin(Long userId, BigDecimal amount, boolean addition, String note);
}
