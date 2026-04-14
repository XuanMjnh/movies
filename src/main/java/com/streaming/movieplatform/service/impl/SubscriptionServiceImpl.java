package com.streaming.movieplatform.service.impl;

import com.streaming.movieplatform.entity.SubscriptionPlan;
import com.streaming.movieplatform.entity.User;
import com.streaming.movieplatform.entity.UserSubscription;
import com.streaming.movieplatform.entity.Voucher;
import com.streaming.movieplatform.entity.Wallet;
import com.streaming.movieplatform.entity.WalletTransaction;
import com.streaming.movieplatform.enums.AccessLevel;
import com.streaming.movieplatform.enums.SubscriptionStatus;
import com.streaming.movieplatform.enums.TransactionStatus;
import com.streaming.movieplatform.enums.TransactionType;
import com.streaming.movieplatform.enums.VoucherAudienceMatchMode;
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
import java.time.temporal.ChronoUnit;
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
    @Transactional(readOnly = true)
    public List<Voucher> getEligibleDisplayVouchers(User user) {
        if (user == null) {
            return List.of();
        }

        VoucherEligibilityContext eligibilityContext = buildEligibilityContext(user);
        LocalDateTime now = LocalDateTime.now();
        return voucherRepository.findByAutoDisplayEnabledTrueOrderByCreatedAtDesc().stream()
                .filter(voucher -> isVoucherCurrentlyUsable(voucher, now))
                .filter(voucher -> isUserEligibleForVoucher(voucher, eligibilityContext))
                .toList();
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
        validateNoDowngrade(current, plan);

        Voucher voucher = resolveVoucher(voucherCode, plan.getPrice(), user);
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
        UserSubscription subscription = findLatestActiveSubscription(user);
        if (subscription == null) {
            return null;
        }
        if (subscription.getEndDate().isBefore(LocalDate.now())) {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            userSubscriptionRepository.save(subscription);
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
        UserSubscription subscription = findLatestActiveSubscription(user);
        if (subscription != null && subscription.getEndDate().isBefore(LocalDate.now())) {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            userSubscriptionRepository.save(subscription);
        }
    }

    private UserSubscription findLatestActiveSubscription(User user) {
        if (user == null) {
            return null;
        }
        return userSubscriptionRepository
                .findFirstByUserIdAndStatusOrderByEndDateDesc(user.getId(), SubscriptionStatus.ACTIVE)
                .orElse(null);
    }

    private void validateNoDowngrade(UserSubscription current, SubscriptionPlan targetPlan) {
        if (current == null || current.getPlan() == null || current.getPlan().getAccessLevel() == null || targetPlan.getAccessLevel() == null) {
            return;
        }
        AccessLevel currentAccessLevel = current.getPlan().getAccessLevel();
        if (currentAccessLevel.ordinal() > targetPlan.getAccessLevel().ordinal()) {
            throw new BusinessException("Bạn đang dùng gói cao hơn nên không thể mua xuống gói thấp hơn");
        }
    }

    private Voucher resolveVoucher(String voucherCode, BigDecimal orderAmount, User user) {
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
        String eligibilityError = buildEligibilityErrorMessage(voucher, buildEligibilityContext(user));
        if (eligibilityError != null) {
            throw new BusinessException(eligibilityError);
        }
        return voucher;
    }

    private boolean isVoucherCurrentlyUsable(Voucher voucher, LocalDateTime now) {
        return voucher.isActive()
                && !voucher.getStartAt().isAfter(now)
                && !voucher.getEndAt().isBefore(now)
                && voucher.getUsedCount() < voucher.getQuantity();
    }

    private VoucherEligibilityContext buildEligibilityContext(User user) {
        BigDecimal totalSpent = walletTransactionRepository.sumAmountByUserIdAndStatusAndType(
                user.getId(),
                TransactionStatus.SUCCESS,
                TransactionType.SUBSCRIPTION_PURCHASE
        );
        long accountAgeDays = user.getCreatedAt() == null
                ? 0
                : ChronoUnit.DAYS.between(user.getCreatedAt().toLocalDate(), LocalDate.now());
        return new VoucherEligibilityContext(totalSpent, accountAgeDays);
    }

    private boolean isUserEligibleForVoucher(Voucher voucher, VoucherEligibilityContext eligibilityContext) {
        boolean hasSpentRule = voucher.getMinTotalSpentAmount() != null
                && voucher.getMinTotalSpentAmount().compareTo(BigDecimal.ZERO) > 0;
        boolean hasAccountAgeRule = voucher.getMinAccountAgeDays() != null
                && voucher.getMinAccountAgeDays() > 0;

        int configuredRuleCount = 0;
        int matchedRuleCount = 0;

        if (hasSpentRule) {
            configuredRuleCount++;
            if (eligibilityContext.totalSpent().compareTo(voucher.getMinTotalSpentAmount()) >= 0) {
                matchedRuleCount++;
            }
        }
        if (hasAccountAgeRule) {
            configuredRuleCount++;
            if (eligibilityContext.accountAgeDays() >= voucher.getMinAccountAgeDays()) {
                matchedRuleCount++;
            }
        }

        if (configuredRuleCount == 0) {
            return true;
        }

        VoucherAudienceMatchMode audienceMatchMode = voucher.getAudienceMatchMode() == null
                ? VoucherAudienceMatchMode.ALL
                : voucher.getAudienceMatchMode();
        if (audienceMatchMode == VoucherAudienceMatchMode.ANY) {
            return matchedRuleCount > 0;
        }
        return matchedRuleCount == configuredRuleCount;
    }

    private String buildEligibilityErrorMessage(Voucher voucher, VoucherEligibilityContext eligibilityContext) {
        if (isUserEligibleForVoucher(voucher, eligibilityContext)) {
            return null;
        }

        boolean hasSpentRule = voucher.getMinTotalSpentAmount() != null
                && voucher.getMinTotalSpentAmount().compareTo(BigDecimal.ZERO) > 0;
        boolean hasAccountAgeRule = voucher.getMinAccountAgeDays() != null
                && voucher.getMinAccountAgeDays() > 0;
        VoucherAudienceMatchMode audienceMatchMode = voucher.getAudienceMatchMode() == null
                ? VoucherAudienceMatchMode.ALL
                : voucher.getAudienceMatchMode();

        if (hasSpentRule && hasAccountAgeRule) {
            if (audienceMatchMode == VoucherAudienceMatchMode.ANY) {
                return "Voucher chưa dành cho tài khoản này. Yêu cầu tổng chi tiêu từ "
                        + voucher.getMinTotalSpentAmount().stripTrailingZeros().toPlainString()
                        + " đ hoặc tài khoản từ "
                        + voucher.getMinAccountAgeDays()
                        + " ngày.";
            }
            return "Voucher yêu cầu tổng chi tiêu từ "
                    + voucher.getMinTotalSpentAmount().stripTrailingZeros().toPlainString()
                    + " đ và tài khoản từ "
                    + voucher.getMinAccountAgeDays()
                    + " ngày.";
        }

        if (hasSpentRule) {
            return "Voucher yêu cầu tổng chi tiêu từ "
                    + voucher.getMinTotalSpentAmount().stripTrailingZeros().toPlainString()
                    + " đ. Hiện tại bạn đã chi "
                    + eligibilityContext.totalSpent().stripTrailingZeros().toPlainString()
                    + " đ.";
        }

        return "Voucher yêu cầu tài khoản tối thiểu "
                + voucher.getMinAccountAgeDays()
                + " ngày. Hiện tại tài khoản có "
                + eligibilityContext.accountAgeDays()
                + " ngày.";
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
    private record VoucherEligibilityContext(BigDecimal totalSpent, long accountAgeDays) {
    }
}
