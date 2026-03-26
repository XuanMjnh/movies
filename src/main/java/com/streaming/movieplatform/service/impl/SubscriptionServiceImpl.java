package com.streaming.movieplatform.service.impl;

import com.streaming.movieplatform.entity.SubscriptionPlan;
import com.streaming.movieplatform.entity.User;
import com.streaming.movieplatform.entity.UserSubscription;
import com.streaming.movieplatform.entity.Voucher;
import com.streaming.movieplatform.entity.Wallet;
import com.streaming.movieplatform.entity.WalletTransaction;
import com.streaming.movieplatform.enums.SubscriptionStatus;
import com.streaming.movieplatform.enums.TransactionStatus;
import com.streaming.movieplatform.enums.TransactionType;
import com.streaming.movieplatform.enums.VoucherDiscountType;
import com.streaming.movieplatform.exception.BusinessException;
import com.streaming.movieplatform.exception.ResourceNotFoundException;
import com.streaming.movieplatform.repository.SubscriptionPlanRepository;
import com.streaming.movieplatform.repository.UserSubscriptionRepository;
import com.streaming.movieplatform.repository.VoucherRepository;
import com.streaming.movieplatform.repository.WalletRepository;
import com.streaming.movieplatform.repository.WalletTransactionRepository;
import com.streaming.movieplatform.service.SubscriptionService;
import com.streaming.movieplatform.util.ReferenceCodeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final VoucherRepository voucherRepository;

    public SubscriptionServiceImpl(SubscriptionPlanRepository subscriptionPlanRepository,
                                   UserSubscriptionRepository userSubscriptionRepository,
                                   WalletRepository walletRepository,
                                   WalletTransactionRepository walletTransactionRepository,
                                   VoucherRepository voucherRepository) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.userSubscriptionRepository = userSubscriptionRepository;
        this.walletRepository = walletRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.voucherRepository = voucherRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionPlan> getActivePlans() {
        return subscriptionPlanRepository.findByActiveTrueOrderByPriceAsc();
    }

    @Override
    public UserSubscription purchasePlan(User user, Long planId, String voucherCode) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy gói thành viên"));
        if (!plan.isActive()) {
            throw new BusinessException("Gói thành viên đã bị vô hiệu hóa");
        }

        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ví tiền"));

        refreshStatus(user);
        UserSubscription current = getCurrentSubscription(user);

        Voucher voucher = resolveVoucher(voucherCode, plan.getPrice());
        BigDecimal discountAmount = calculateDiscount(plan.getPrice(), voucher);
        BigDecimal finalAmount = plan.getPrice().subtract(discountAmount).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

        if (wallet.getBalance().compareTo(finalAmount) < 0) {
            throw new BusinessException("Số dư ví không đủ để mua gói thành viên này");
        }

        wallet.setBalance(wallet.getBalance().subtract(finalAmount));
        walletRepository.save(wallet);

        WalletTransaction walletTransaction = new WalletTransaction();
        walletTransaction.setWallet(wallet);
        walletTransaction.setUser(user);
        walletTransaction.setType(TransactionType.SUBSCRIPTION_PURCHASE);
        walletTransaction.setStatus(TransactionStatus.SUCCESS);
        walletTransaction.setAmount(finalAmount);
        walletTransaction.setBalanceAfter(wallet.getBalance());
        walletTransaction.setReferenceCode(ReferenceCodeUtil.generate("SUB"));
        walletTransaction.setDescription(buildSubscriptionDescription(plan, voucher, discountAmount));
        walletTransactionRepository.save(walletTransaction);

        if (voucher != null) {
            voucher.setUsedCount(voucher.getUsedCount() + 1);
            voucherRepository.save(voucher);
        }

        if (current != null && current.getPlan().getId().equals(plan.getId())) {
            current.setEndDate(current.getEndDate().plusDays(plan.getDurationDays()));
            current.setPaidAmount(current.getPaidAmount().add(finalAmount));
            return userSubscriptionRepository.save(current);
        }

        if (current != null) {
            current.setStatus(SubscriptionStatus.EXPIRED);
            userSubscriptionRepository.save(current);
        }

        UserSubscription newSubscription = new UserSubscription();
        newSubscription.setUser(user);
        newSubscription.setPlan(plan);
        newSubscription.setStartDate(LocalDate.now());
        newSubscription.setEndDate(LocalDate.now().plusDays(plan.getDurationDays()));
        newSubscription.setStatus(SubscriptionStatus.ACTIVE);
        newSubscription.setPaidAmount(finalAmount);
        return userSubscriptionRepository.save(newSubscription);
    }

    @Override
    @Transactional(readOnly = true)
    public UserSubscription getCurrentSubscription(User user) {
        UserSubscription subscription = userSubscriptionRepository
                .findFirstByUserIdAndStatusOrderByEndDateDesc(user.getId(), SubscriptionStatus.ACTIVE)
                .orElse(null);
        if (subscription == null) {
            return null;
        }
        if (subscription.getEndDate().isBefore(LocalDate.now())) {
            return null;
        }
        return subscription;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSubscription> getSubscriptionHistory(User user) {
        return userSubscriptionRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSubscription> getLatestSubscriptions() {
        return userSubscriptionRepository.findTop20ByOrderByCreatedAtDesc();
    }

    @Override
    public void refreshStatus(User user) {
        UserSubscription subscription = userSubscriptionRepository
                .findFirstByUserIdAndStatusOrderByEndDateDesc(user.getId(), SubscriptionStatus.ACTIVE)
                .orElse(null);
        if (subscription != null && subscription.getEndDate().isBefore(LocalDate.now())) {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            userSubscriptionRepository.save(subscription);
        }
    }

    private Voucher resolveVoucher(String voucherCode, BigDecimal orderAmount) {
        if (!StringUtils.hasText(voucherCode)) {
            return null;
        }
        Voucher voucher = voucherRepository.findByCodeIgnoreCase(voucherCode.trim())
                .orElseThrow(() -> new BusinessException("Voucher không tồn tại"));

        LocalDateTime now = LocalDateTime.now();
        if (!voucher.isActive()) {
            throw new BusinessException("Voucher hiện đang bị khóa");
        }
        if (voucher.getStartAt().isAfter(now)) {
            throw new BusinessException("Voucher chưa đến thời gian sử dụng");
        }
        if (voucher.getEndAt().isBefore(now)) {
            throw new BusinessException("Voucher đã hết hạn");
        }
        if (voucher.getUsedCount() >= voucher.getQuantity()) {
            throw new BusinessException("Voucher đã hết lượt sử dụng");
        }
        if (orderAmount.compareTo(voucher.getMinOrderAmount()) < 0) {
            throw new BusinessException("Đơn hàng chưa đạt giá trị tối thiểu để áp voucher");
        }
        return voucher;
    }

    private BigDecimal calculateDiscount(BigDecimal orderAmount, Voucher voucher) {
        if (voucher == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount;
        if (voucher.getDiscountType() == VoucherDiscountType.PERCENT) {
            BigDecimal percent = voucher.getDiscountValue().min(new BigDecimal("100"));
            discount = orderAmount.multiply(percent).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        } else {
            discount = voucher.getDiscountValue();
        }

        if (voucher.getMaxDiscountAmount() != null && discount.compareTo(voucher.getMaxDiscountAmount()) > 0) {
            discount = voucher.getMaxDiscountAmount();
        }
        if (discount.compareTo(orderAmount) > 0) {
            discount = orderAmount;
        }
        return discount.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    private String buildSubscriptionDescription(SubscriptionPlan plan, Voucher voucher, BigDecimal discountAmount) {
        String description = "Thanh toán gói " + plan.getName();
        if (voucher != null) {
            description += " - áp voucher " + voucher.getCode() + " giảm " + discountAmount.stripTrailingZeros().toPlainString() + " đ";
        }
        return description;
    }
}
