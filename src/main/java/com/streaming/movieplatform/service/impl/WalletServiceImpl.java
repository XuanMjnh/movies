package com.streaming.movieplatform.service.impl;

import com.streaming.movieplatform.config.VnPayProperties;
import com.streaming.movieplatform.dto.DepositRequest;
import com.streaming.movieplatform.dto.VnPayIpnResponse;
import com.streaming.movieplatform.dto.VnPayReturnResult;
import com.streaming.movieplatform.entity.PaymentTransaction;
import com.streaming.movieplatform.entity.User;
import com.streaming.movieplatform.entity.Wallet;
import com.streaming.movieplatform.entity.WalletTransaction;
import com.streaming.movieplatform.enums.TransactionStatus;
import com.streaming.movieplatform.enums.TransactionType;
import com.streaming.movieplatform.exception.BusinessException;
import com.streaming.movieplatform.exception.ResourceNotFoundException;
import com.streaming.movieplatform.repository.PaymentTransactionRepository;
import com.streaming.movieplatform.repository.UserRepository;
import com.streaming.movieplatform.repository.WalletRepository;
import com.streaming.movieplatform.repository.WalletTransactionRepository;
import com.streaming.movieplatform.service.WalletService;
import com.streaming.movieplatform.util.ReferenceCodeUtil;
import com.streaming.movieplatform.util.VnPayUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class WalletServiceImpl implements WalletService {

    private static final String VNPAY_PROVIDER = "VNPAY";

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final UserRepository userRepository;
    private final VnPayProperties vnPayProperties;

    @Value("${app.deposit-payment-provider:SIMULATED_GATEWAY}")
    private String paymentProvider;

    public WalletServiceImpl(WalletRepository walletRepository,
                             WalletTransactionRepository walletTransactionRepository,
                             PaymentTransactionRepository paymentTransactionRepository,
                             UserRepository userRepository,
                             VnPayProperties vnPayProperties) {
        this.walletRepository = walletRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.userRepository = userRepository;
        this.vnPayProperties = vnPayProperties;
    }

    @Override
    @Transactional(readOnly = true)
    public Wallet getWallet(User user) {
        if (user == null) {
            throw new BusinessException("Bạn cần đăng nhập để sử dụng ví tiền");
        }
        return walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ví của người dùng"));
    }

    @Override
    public WalletTransaction deposit(User user, DepositRequest request) {
        Wallet wallet = getWallet(user);
        BigDecimal newBalance = wallet.getBalance().add(request.getAmount());
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);

        String reference = ReferenceCodeUtil.generate("DEP");

        WalletTransaction walletTransaction = new WalletTransaction();
        walletTransaction.setWallet(wallet);
        walletTransaction.setUser(user);
        walletTransaction.setType(TransactionType.DEPOSIT);
        walletTransaction.setStatus(TransactionStatus.SUCCESS);
        walletTransaction.setAmount(request.getAmount());
        walletTransaction.setBalanceAfter(newBalance);
        walletTransaction.setReferenceCode(reference);
        walletTransaction.setDescription("Nạp tiền mô phỏng vào ví thành công");
        walletTransactionRepository.save(walletTransaction);

        PaymentTransaction paymentTransaction = new PaymentTransaction();
        paymentTransaction.setUser(user);
        paymentTransaction.setWalletTransaction(walletTransaction);
        paymentTransaction.setAmount(request.getAmount());
        paymentTransaction.setProvider(paymentProvider);
        paymentTransaction.setExternalReference(reference);
        paymentTransaction.setStatus(TransactionStatus.SUCCESS);
        paymentTransactionRepository.save(paymentTransaction);

        return walletTransaction;
    }

    @Override
    public String createVnPayDepositPaymentUrl(User user, DepositRequest request, String clientIp) {
        if (!vnPayProperties.isConfigured()) {
            throw new BusinessException("VNPAY chưa được cấu hình đầy đủ. Hãy cập nhật VNPAY_TMN_CODE, VNPAY_HASH_SECRET và VNPAY_RETURN_URL trong file .env");
        }

        Wallet wallet = getWallet(user);
        String reference = VnPayUtil.generateTxnRef();

        WalletTransaction walletTransaction = new WalletTransaction();
        walletTransaction.setWallet(wallet);
        walletTransaction.setUser(user);
        walletTransaction.setType(TransactionType.DEPOSIT);
        walletTransaction.setStatus(TransactionStatus.PENDING);
        walletTransaction.setAmount(request.getAmount());
        walletTransaction.setBalanceAfter(wallet.getBalance());
        walletTransaction.setReferenceCode(reference);
        walletTransaction.setDescription("Khoi tao giao dich nap tien qua VNPAY");
        walletTransaction = walletTransactionRepository.save(walletTransaction);

        PaymentTransaction paymentTransaction = new PaymentTransaction();
        paymentTransaction.setUser(user);
        paymentTransaction.setWalletTransaction(walletTransaction);
        paymentTransaction.setAmount(request.getAmount());
        paymentTransaction.setProvider(VNPAY_PROVIDER);
        paymentTransaction.setExternalReference(reference);
        paymentTransaction.setStatus(TransactionStatus.PENDING);
        paymentTransactionRepository.save(paymentTransaction);

        LocalDateTime now = VnPayUtil.nowInVnPayZone();
        Map<String, String> params = new LinkedHashMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", vnPayProperties.getTmnCode());
        params.put("vnp_Amount", request.getAmount().movePointRight(2).toBigIntegerExact().toString());
        params.put("vnp_CreateDate", VnPayUtil.formatDate(now));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_IpAddr", StringUtils.hasText(clientIp) ? clientIp : "127.0.0.1");
        params.put("vnp_Locale", vnPayProperties.getLocale());
        params.put("vnp_OrderInfo", VnPayUtil.sanitizeOrderInfo("Nap tien vi cho " + user.getEmail() + " ma " + reference));
        params.put("vnp_OrderType", vnPayProperties.getOrderType());
        params.put("vnp_ReturnUrl", vnPayProperties.getReturnUrl());
        params.put("vnp_ExpireDate", VnPayUtil.formatDate(now.plusMinutes(vnPayProperties.getExpireMinutes())));
        params.put("vnp_TxnRef", reference);

        return VnPayUtil.buildPaymentUrl(vnPayProperties.getPayUrl(), params, vnPayProperties.getHashSecret());
    }

    @Override
    public VnPayIpnResponse handleVnPayIpn(Map<String, String> params) {
        if (!VnPayUtil.verifySignature(params, vnPayProperties.getHashSecret())) {
            return new VnPayIpnResponse("97", "Invalid signature");
        }

        String reference = params.get("vnp_TxnRef");
        PaymentTransaction paymentTransaction = paymentTransactionRepository.findByExternalReference(reference)
                .orElse(null);
        if (paymentTransaction == null) {
            return new VnPayIpnResponse("01", "Order not found");
        }

        BigDecimal gatewayAmount = parseVnPayAmount(params.get("vnp_Amount"));
        if (gatewayAmount == null || gatewayAmount.compareTo(paymentTransaction.getAmount()) != 0) {
            return new VnPayIpnResponse("04", "Invalid amount");
        }

        if (paymentTransaction.getStatus() != TransactionStatus.PENDING) {
            return new VnPayIpnResponse("02", "Order already confirmed");
        }

        WalletTransaction walletTransaction = paymentTransaction.getWalletTransaction();
        boolean success = isGatewaySuccess(params);
        if (success) {
            Wallet wallet = walletTransaction.getWallet();
            BigDecimal newBalance = wallet.getBalance().add(paymentTransaction.getAmount());
            wallet.setBalance(newBalance);
            walletRepository.save(wallet);

            walletTransaction.setStatus(TransactionStatus.SUCCESS);
            walletTransaction.setBalanceAfter(newBalance);
            walletTransaction.setDescription("Nap tien vao vi qua VNPAY thanh cong");
            walletTransactionRepository.save(walletTransaction);

            paymentTransaction.setStatus(TransactionStatus.SUCCESS);
            paymentTransactionRepository.save(paymentTransaction);
            return new VnPayIpnResponse("00", "Confirm Success");
        }

        TransactionStatus failedStatus = "24".equals(params.get("vnp_ResponseCode"))
                ? TransactionStatus.CANCELLED
                : TransactionStatus.FAILED;
        walletTransaction.setStatus(failedStatus);
        walletTransaction.setBalanceAfter(walletTransaction.getWallet().getBalance());
        walletTransaction.setDescription(buildFailedDescription(params.get("vnp_ResponseCode")));
        walletTransactionRepository.save(walletTransaction);

        paymentTransaction.setStatus(failedStatus);
        paymentTransactionRepository.save(paymentTransaction);
        return new VnPayIpnResponse("00", "Confirm Success");
    }

    @Override
    @Transactional(readOnly = true)
    public VnPayReturnResult getVnPayReturnResult(Map<String, String> params) {
        VnPayReturnResult result = new VnPayReturnResult();
        result.setTxnRef(params.get("vnp_TxnRef"));
        result.setResponseCode(params.get("vnp_ResponseCode"));
        result.setTransactionStatus(params.get("vnp_TransactionStatus"));
        result.setAmount(parseVnPayAmount(params.get("vnp_Amount")));

        boolean validSignature = VnPayUtil.verifySignature(params, vnPayProperties.getHashSecret());
        result.setValidSignature(validSignature);
        if (!validSignature) {
            result.setMessage("Chu ky du lieu tra ve khong hop le.");
            return result;
        }

        PaymentTransaction paymentTransaction = paymentTransactionRepository.findByExternalReference(result.getTxnRef())
                .orElse(null);
        result.setPaymentTransaction(paymentTransaction);

        if (paymentTransaction == null) {
            result.setMessage("Khong tim thay giao dich VNPAY trong he thong.");
            return result;
        }

        boolean gatewaySuccess = isGatewaySuccess(params);
        if (paymentTransaction.getStatus() == TransactionStatus.SUCCESS) {
            result.setSuccess(true);
            result.setMessage("Thanh toan thanh cong. So du vi da duoc cap nhat.");
            return result;
        }

        if (gatewaySuccess && paymentTransaction.getStatus() == TransactionStatus.PENDING) {
            result.setPendingConfirmation(true);
            result.setMessage("VNPAY da tra ve ket qua thanh cong. He thong dang cho IPN xac nhan de cap nhat vi.");
            return result;
        }

        if (paymentTransaction.getStatus() == TransactionStatus.CANCELLED || "24".equals(result.getResponseCode())) {
            result.setMessage("Ban da huy giao dich thanh toan tren VNPAY.");
            return result;
        }

        result.setMessage("Thanh toan chua thanh cong. Vui long thu lai hoac kiem tra lich su giao dich.");
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalletTransaction> getTransactions(User user) {
        return walletTransactionRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalletTransaction> getLatestTransactions() {
        return walletTransactionRepository.findTop10ByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentTransaction> getPaymentTransactions() {
        return paymentTransactionRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public WalletTransaction adjustBalanceByAdmin(Long userId, BigDecimal amount, boolean addition, String note) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Số tiền điều chỉnh phải lớn hơn 0");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        Wallet wallet = getWallet(user);

        BigDecimal newBalance = addition
                ? wallet.getBalance().add(amount)
                : wallet.getBalance().subtract(amount);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Không thể trừ vượt quá số dư hiện tại của người dùng");
        }

        wallet.setBalance(newBalance);
        walletRepository.save(wallet);

        WalletTransaction walletTransaction = new WalletTransaction();
        walletTransaction.setWallet(wallet);
        walletTransaction.setUser(user);
        walletTransaction.setType(TransactionType.ADJUSTMENT);
        walletTransaction.setStatus(TransactionStatus.SUCCESS);
        walletTransaction.setAmount(amount);
        walletTransaction.setBalanceAfter(newBalance);
        walletTransaction.setReferenceCode(ReferenceCodeUtil.generate(addition ? "ADMADD" : "ADMSUB"));
        walletTransaction.setDescription((addition ? "Admin cộng tiền" : "Admin trừ tiền")
                + (StringUtils.hasText(note) ? ": " + note.trim() : ""));
        return walletTransactionRepository.save(walletTransaction);
    }

    private boolean isGatewaySuccess(Map<String, String> params) {
        return "00".equals(params.get("vnp_ResponseCode")) && "00".equals(params.get("vnp_TransactionStatus"));
    }

    private BigDecimal parseVnPayAmount(String rawAmount) {
        if (!StringUtils.hasText(rawAmount)) {
            return null;
        }
        try {
            return new BigDecimal(rawAmount).movePointLeft(2);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String buildFailedDescription(String responseCode) {
        if ("24".equals(responseCode)) {
            return "Nguoi dung huy giao dich nap tien qua VNPAY";
        }
        return "Giao dich nap tien qua VNPAY that bai";
    }
}
