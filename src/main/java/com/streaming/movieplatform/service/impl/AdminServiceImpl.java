package com.streaming.movieplatform.service.impl;

import com.streaming.movieplatform.dto.AdminBannerRequest;
import com.streaming.movieplatform.dto.AdminChartItem;
import com.streaming.movieplatform.dto.AdminDashboardStats;
import com.streaming.movieplatform.dto.AdminGenreRequest;
import com.streaming.movieplatform.dto.AdminPlanRequest;
import com.streaming.movieplatform.dto.AdminRevenueItem;
import com.streaming.movieplatform.dto.AdminUserUpdateRequest;
import com.streaming.movieplatform.dto.AdminVoucherRequest;
import com.streaming.movieplatform.entity.Banner;
import com.streaming.movieplatform.entity.Genre;
import com.streaming.movieplatform.entity.Movie;
import com.streaming.movieplatform.entity.Role;
import com.streaming.movieplatform.entity.SubscriptionPlan;
import com.streaming.movieplatform.entity.User;
import com.streaming.movieplatform.entity.UserSubscription;
import com.streaming.movieplatform.entity.Voucher;
import com.streaming.movieplatform.entity.WalletTransaction;
import com.streaming.movieplatform.enums.RoleName;
import com.streaming.movieplatform.enums.SubscriptionStatus;
import com.streaming.movieplatform.enums.TransactionStatus;
import com.streaming.movieplatform.enums.TransactionType;
import com.streaming.movieplatform.exception.BusinessException;
import com.streaming.movieplatform.exception.ResourceNotFoundException;
import com.streaming.movieplatform.repository.BannerRepository;
import com.streaming.movieplatform.repository.CommentRepository;
import com.streaming.movieplatform.repository.GenreRepository;
import com.streaming.movieplatform.repository.MovieRepository;
import com.streaming.movieplatform.repository.RoleRepository;
import com.streaming.movieplatform.repository.SubscriptionPlanRepository;
import com.streaming.movieplatform.repository.UserRepository;
import com.streaming.movieplatform.repository.UserSubscriptionRepository;
import com.streaming.movieplatform.repository.VoucherRepository;
import com.streaming.movieplatform.repository.WalletTransactionRepository;
import com.streaming.movieplatform.service.AdminService;
import com.streaming.movieplatform.service.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final GenreRepository genreRepository;
    private final BannerRepository bannerRepository;
    private final MovieRepository movieRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final VoucherRepository voucherRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final CommentRepository commentRepository;
    private final StorageService storageService;

    public AdminServiceImpl(UserRepository userRepository,
                            RoleRepository roleRepository,
                            GenreRepository genreRepository,
                            BannerRepository bannerRepository,
                            MovieRepository movieRepository,
                            SubscriptionPlanRepository subscriptionPlanRepository,
                            UserSubscriptionRepository userSubscriptionRepository,
                            VoucherRepository voucherRepository,
                            WalletTransactionRepository walletTransactionRepository,
                            CommentRepository commentRepository,
                            StorageService storageService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.genreRepository = genreRepository;
        this.bannerRepository = bannerRepository;
        this.movieRepository = movieRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.userSubscriptionRepository = userSubscriptionRepository;
        this.voucherRepository = voucherRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.commentRepository = commentRepository;
        this.storageService = storageService;
    }

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardStats getDashboardStats() {
        AdminDashboardStats stats = new AdminDashboardStats();
        List<User> users = userRepository.findAll();
        List<Movie> movies = movieRepository.findAll();
        List<WalletTransaction> revenueTransactions = getSuccessfulRevenueTransactions();
        List<UserSubscription> subscriptions = userSubscriptionRepository.findAll();
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.now();

        stats.setTotalUsers(users.size());
        stats.setTotalMovies(movies.size());
        stats.setTotalComments(commentRepository.count());
        stats.setTotalViewCount(movies.stream().mapToLong(Movie::getViewCount).sum());
        stats.setWalletRevenue(sumTransactionAmounts(revenueTransactions));
        stats.setRevenueToday(sumTransactionAmounts(revenueTransactions.stream()
                .filter(tx -> tx.getCreatedAt().toLocalDate().equals(today))
                .toList()));
        stats.setRevenueThisMonth(sumTransactionAmounts(revenueTransactions.stream()
                .filter(tx -> YearMonth.from(tx.getCreatedAt()).equals(currentMonth))
                .toList()));
        stats.setActiveSubscriptions(subscriptions.stream()
                .filter(sub -> sub.getStatus() == SubscriptionStatus.ACTIVE && !sub.getEndDate().isBefore(today))
                .count());
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminRevenueItem> getDailyRevenueStats() {
        Map<LocalDate, BigDecimal> revenueByDate = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            revenueByDate.put(today.minusDays(i), BigDecimal.ZERO);
        }

        for (WalletTransaction tx : getSuccessfulRevenueTransactions()) {
            LocalDate txDate = tx.getCreatedAt().toLocalDate();
            if (revenueByDate.containsKey(txDate)) {
                revenueByDate.put(txDate, revenueByDate.get(txDate).add(tx.getAmount()));
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<AdminRevenueItem> items = new ArrayList<>();
        revenueByDate.forEach((date, amount) -> items.add(new AdminRevenueItem(formatter.format(date), amount)));
        return items;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminRevenueItem> getMonthlyRevenueStats() {
        Map<YearMonth, BigDecimal> revenueByMonth = new LinkedHashMap<>();
        YearMonth now = YearMonth.now();
        for (int i = 5; i >= 0; i--) {
            revenueByMonth.put(now.minusMonths(i), BigDecimal.ZERO);
        }

        for (WalletTransaction tx : getSuccessfulRevenueTransactions()) {
            YearMonth txMonth = YearMonth.from(tx.getCreatedAt());
            if (revenueByMonth.containsKey(txMonth)) {
                revenueByMonth.put(txMonth, revenueByMonth.get(txMonth).add(tx.getAmount()));
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        List<AdminRevenueItem> items = new ArrayList<>();
        revenueByMonth.forEach((month, amount) -> items.add(new AdminRevenueItem(formatter.format(month), amount)));
        return items;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminChartItem> getMovieTypeStats() {
        Map<String, Long> movieTypeCounts = new LinkedHashMap<>();
        movieTypeCounts.put("Phim lẻ", 0L);
        movieTypeCounts.put("Phim bộ", 0L);

        for (Movie movie : movieRepository.findAll()) {
            String label = movie.getMovieType() == com.streaming.movieplatform.enums.MovieType.SERIES
                    ? "Phim bộ"
                    : "Phim lẻ";
            movieTypeCounts.put(label, movieTypeCounts.get(label) + 1);
        }

        List<AdminChartItem> items = new ArrayList<>();
        movieTypeCounts.forEach((label, value) -> items.add(new AdminChartItem(label, value)));
        return items;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminChartItem> getTopViewedMovieStats() {
        return movieRepository.findTop5ByOrderByViewCountDesc().stream()
                .map(movie -> new AdminChartItem(movie.getTitle(), movie.getViewCount()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }

    @Override
    public User updateUser(AdminUserUpdateRequest request) {
        User user = getUserById(request.getId());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setEnabled(request.isEnabled());

        Set<Role> roles = new HashSet<>();
        java.util.List<String> requestedRoles = request.getRoleNames() == null ? java.util.List.of() : request.getRoleNames();
        for (String roleName : requestedRoles) {
            roles.add(roleRepository.findByName(RoleName.valueOf(roleName))
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy role " + roleName)));
        }
        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Genre> getAllGenres() {
        return genreRepository.findAllByOrderByNameAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Genre getGenreById(Long genreId) {
        return genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại"));
    }

    @Override
    public Genre saveGenre(AdminGenreRequest request) {
        Genre genre = request.getId() == null ? new Genre() : getGenreById(request.getId());
        genre.setName(request.getName());
        genre.setDescription(request.getDescription());
        return genreRepository.save(genre);
    }

    @Override
    public void deleteGenre(Long genreId) {
        genreRepository.deleteById(genreId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Banner> getAllBanners() {
        return bannerRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Banner getBannerById(Long bannerId) {
        return bannerRepository.findById(bannerId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy banner"));
    }

    @Override
    public Banner saveBanner(AdminBannerRequest request) {
        Banner banner = request.getId() == null ? new Banner() : getBannerById(request.getId());
        banner.setTitle(request.getTitle());
        banner.setSubtitle(request.getSubtitle());
        banner.setImageUrl(storageService.store(request.getImageFile(), "banners", request.getExistingImageUrl()));
        banner.setCtaText(request.getCtaText());
        banner.setCtaLink(request.getCtaLink());
        banner.setDisplayOrder(request.getDisplayOrder());
        banner.setActive(request.isActive());
        if (request.getMovieId() != null) {
            Movie movie = movieRepository.findById(request.getMovieId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim liên kết banner"));
            banner.setMovie(movie);
        } else {
            banner.setMovie(null);
        }
        return bannerRepository.save(banner);
    }

    @Override
    public void deleteBanner(Long bannerId) {
        bannerRepository.deleteById(bannerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionPlan> getAllPlans() {
        return subscriptionPlanRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionPlan getPlanById(Long planId) {
        return subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy gói thành viên"));
    }

    @Override
    public SubscriptionPlan savePlan(AdminPlanRequest request) {
        SubscriptionPlan plan = request.getId() == null ? new SubscriptionPlan() : getPlanById(request.getId());
        plan.setName(request.getName());
        plan.setAccessLevel(request.getAccessLevel());
        plan.setPrice(request.getPrice());
        plan.setDurationDays(request.getDurationDays());
        plan.setActive(request.isActive());
        plan.setFeatureDescription(request.getFeatureDescription());
        return subscriptionPlanRepository.save(plan);
    }

    @Override
    public void deletePlan(Long planId) {
        subscriptionPlanRepository.deleteById(planId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Voucher getVoucherById(Long voucherId) {
        return voucherRepository.findById(voucherId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy voucher"));
    }

    @Override
    public Voucher saveVoucher(AdminVoucherRequest request) {
        if (request.getEndAt().isBefore(request.getStartAt()) || request.getEndAt().isEqual(request.getStartAt())) {
            throw new BusinessException("Thời gian kết thúc phải sau thời gian bắt đầu");
        }
        if (request.getDiscountType() == null) {
            throw new BusinessException("Loại giảm giá không hợp lệ");
        }
        if (request.getDiscountValue() == null || request.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Giá trị giảm phải lớn hơn 0");
        }
        if (request.getDiscountType().name().equals("PERCENT") && request.getDiscountValue().compareTo(new BigDecimal("100")) > 0) {
            throw new BusinessException("Voucher theo phần trăm không được lớn hơn 100");
        }

        String normalizedCode = request.getCode().trim().toUpperCase(Locale.ROOT);
        voucherRepository.findByCodeIgnoreCase(normalizedCode)
                .filter(existing -> request.getId() == null || !existing.getId().equals(request.getId()))
                .ifPresent(existing -> {
                    throw new BusinessException("Mã voucher đã tồn tại");
                });

        Voucher voucher = request.getId() == null ? new Voucher() : getVoucherById(request.getId());

        voucher.setCode(normalizedCode);
        voucher.setName(request.getName().trim());
        voucher.setDescription(StringUtils.hasText(request.getDescription()) ? request.getDescription().trim() : null);
        voucher.setDiscountType(request.getDiscountType());
        voucher.setDiscountValue(request.getDiscountValue());
        voucher.setMaxDiscountAmount(request.getMaxDiscountAmount());
        voucher.setMinOrderAmount(request.getMinOrderAmount());
        voucher.setQuantity(request.getQuantity());
        voucher.setActive(request.isActive());
        voucher.setStartAt(request.getStartAt());
        voucher.setEndAt(request.getEndAt());
        if (voucher.getUsedCount() == null) {
            voucher.setUsedCount(0);
        }
        if (voucher.getUsedCount() > voucher.getQuantity()) {
            throw new BusinessException("Số lượng mới không được nhỏ hơn số lượt đã dùng");
        }
        return voucherRepository.save(voucher);
    }

    @Override
    public void deleteVoucher(Long voucherId) {
        voucherRepository.deleteById(voucherId);
    }

    private List<WalletTransaction> getSuccessfulRevenueTransactions() {
        return walletTransactionRepository.findByStatusAndTypeOrderByCreatedAtDesc(
                TransactionStatus.SUCCESS,
                TransactionType.SUBSCRIPTION_PURCHASE
        );
    }

    private BigDecimal sumTransactionAmounts(List<WalletTransaction> transactions) {
        return transactions.stream()
                .map(WalletTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
