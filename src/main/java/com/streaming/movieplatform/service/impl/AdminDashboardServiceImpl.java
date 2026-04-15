package com.streaming.movieplatform.service.impl;

import com.streaming.movieplatform.dto.AdminChartItem;
import com.streaming.movieplatform.dto.AdminDashboardStats;
import com.streaming.movieplatform.dto.AdminRevenueItem;
import com.streaming.movieplatform.entity.Movie;
import com.streaming.movieplatform.entity.User;
import com.streaming.movieplatform.entity.UserSubscription;
import com.streaming.movieplatform.entity.WalletTransaction;
import com.streaming.movieplatform.enums.SubscriptionStatus;
import com.streaming.movieplatform.enums.TransactionStatus;
import com.streaming.movieplatform.enums.TransactionType;
import com.streaming.movieplatform.repository.CommentRepository;
import com.streaming.movieplatform.repository.MovieRepository;
import com.streaming.movieplatform.repository.UserRepository;
import com.streaming.movieplatform.repository.UserSubscriptionRepository;
import com.streaming.movieplatform.repository.WalletTransactionRepository;
import com.streaming.movieplatform.service.AdminDashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final CommentRepository commentRepository;

    public AdminDashboardServiceImpl(UserRepository userRepository,
                                     MovieRepository movieRepository,
                                     UserSubscriptionRepository userSubscriptionRepository,
                                     WalletTransactionRepository walletTransactionRepository,
                                     CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.userSubscriptionRepository = userSubscriptionRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.commentRepository = commentRepository;
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
        movieTypeCounts.put("Phim le", 0L);
        movieTypeCounts.put("Phim bo", 0L);

        for (Movie movie : movieRepository.findAll()) {
            String label = movie.getMovieType() == com.streaming.movieplatform.enums.MovieType.SERIES
                    ? "Phim bo"
                    : "Phim le";
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
