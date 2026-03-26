package com.streaming.movieplatform.config;

import com.streaming.movieplatform.entity.Comment;
import com.streaming.movieplatform.entity.Episode;
import com.streaming.movieplatform.entity.Favorite;
import com.streaming.movieplatform.entity.Movie;
import com.streaming.movieplatform.entity.Rating;
import com.streaming.movieplatform.entity.Role;
import com.streaming.movieplatform.entity.SubscriptionPlan;
import com.streaming.movieplatform.entity.User;
import com.streaming.movieplatform.entity.UserSubscription;
import com.streaming.movieplatform.entity.Wallet;
import com.streaming.movieplatform.entity.WalletTransaction;
import com.streaming.movieplatform.enums.RoleName;
import com.streaming.movieplatform.enums.SubscriptionStatus;
import com.streaming.movieplatform.enums.TransactionStatus;
import com.streaming.movieplatform.enums.TransactionType;
import com.streaming.movieplatform.repository.CommentRepository;
import com.streaming.movieplatform.repository.EpisodeRepository;
import com.streaming.movieplatform.repository.FavoriteRepository;
import com.streaming.movieplatform.repository.MovieRepository;
import com.streaming.movieplatform.repository.RatingRepository;
import com.streaming.movieplatform.repository.RoleRepository;
import com.streaming.movieplatform.repository.SubscriptionPlanRepository;
import com.streaming.movieplatform.repository.UserRepository;
import com.streaming.movieplatform.repository.UserSubscriptionRepository;
import com.streaming.movieplatform.repository.WalletRepository;
import com.streaming.movieplatform.repository.WatchHistoryRepository;
import com.streaming.movieplatform.repository.WalletTransactionRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataSeeder implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final MovieRepository movieRepository;
    private final EpisodeRepository episodeRepository;
    private final FavoriteRepository favoriteRepository;
    private final CommentRepository commentRepository;
    private final RatingRepository ratingRepository;
    private final WatchHistoryRepository watchHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(RoleRepository roleRepository,
                      UserRepository userRepository,
                      WalletRepository walletRepository,
                      WalletTransactionRepository walletTransactionRepository,
                      SubscriptionPlanRepository subscriptionPlanRepository,
                      UserSubscriptionRepository userSubscriptionRepository,
                      MovieRepository movieRepository,
                      EpisodeRepository episodeRepository,
                      FavoriteRepository favoriteRepository,
                      CommentRepository commentRepository,
                      RatingRepository ratingRepository,
                      WatchHistoryRepository watchHistoryRepository,
                      PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.userSubscriptionRepository = userSubscriptionRepository;
        this.movieRepository = movieRepository;
        this.episodeRepository = episodeRepository;
        this.favoriteRepository = favoriteRepository;
        this.commentRepository = commentRepository;
        this.ratingRepository = ratingRepository;
        this.watchHistoryRepository = watchHistoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedRoles();
        User admin = seedUser("admin@example.com", "admin123", "Platform Admin", RoleName.ROLE_ADMIN, BigDecimal.ZERO);
        User user = seedUser("user@example.com", "user123", "Demo User", RoleName.ROLE_USER, new BigDecimal("300000"));
        seedUserContent(user);
        seedSubscriptionHistory(user);
    }

    private void seedRoles() {
        createRoleIfMissing(RoleName.ROLE_GUEST, "Guest role");
        createRoleIfMissing(RoleName.ROLE_USER, "User role");
        createRoleIfMissing(RoleName.ROLE_ADMIN, "Admin role");
    }

    private void createRoleIfMissing(RoleName roleName, String description) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            Role role = new Role();
            role.setName(roleName);
            role.setDescription(description);
            roleRepository.save(role);
        }
    }

    private User seedUser(String email, String rawPassword, String fullName, RoleName roleName, BigDecimal initialBalance) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setFullName(fullName);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setEnabled(true);
            user.getRoles().add(roleRepository.findByName(roleName).orElseThrow());
            user = userRepository.save(user);
        }

        Wallet wallet = walletRepository.findByUserId(user.getId()).orElse(null);
        if (wallet == null) {
            wallet = new Wallet();
            wallet.setUser(user);
            wallet.setBalance(initialBalance);
            wallet = walletRepository.save(wallet);
        } else if (wallet.getBalance().compareTo(initialBalance) < 0) {
            wallet.setBalance(initialBalance);
            walletRepository.save(wallet);
        }

        if (initialBalance.compareTo(BigDecimal.ZERO) > 0 && walletTransactionRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).isEmpty()) {
            WalletTransaction tx = new WalletTransaction();
            tx.setWallet(wallet);
            tx.setUser(user);
            tx.setType(TransactionType.DEPOSIT);
            tx.setStatus(TransactionStatus.SUCCESS);
            tx.setAmount(initialBalance);
            tx.setBalanceAfter(initialBalance);
            tx.setReferenceCode("SEED-DEP-" + user.getId());
            tx.setDescription("Số dư demo để test mua gói thành viên");
            tx.setCreatedAt(LocalDateTime.now().minusDays(5));
            tx.setUpdatedAt(LocalDateTime.now().minusDays(5));
            walletTransactionRepository.save(tx);
        }
        return user;
    }

    private void seedSubscriptionHistory(User user) {
        if (!userSubscriptionRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).isEmpty()) {
            return;
        }
        List<SubscriptionPlan> plans = subscriptionPlanRepository.findAll();
        SubscriptionPlan standard = plans.stream().filter(p -> "Standard".equalsIgnoreCase(p.getName())).findFirst().orElse(null);
        if (standard == null) {
            return;
        }
        UserSubscription history = new UserSubscription();
        history.setUser(user);
        history.setPlan(standard);
        history.setStartDate(LocalDate.now().minusDays(60));
        history.setEndDate(LocalDate.now().minusDays(30));
        history.setStatus(SubscriptionStatus.EXPIRED);
        history.setPaidAmount(standard.getPrice());
        history.setCreatedAt(LocalDateTime.now().minusDays(60));
        history.setUpdatedAt(LocalDateTime.now().minusDays(30));
        userSubscriptionRepository.save(history);
    }

    private void seedUserContent(User user) {
        if (!favoriteRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).isEmpty() && !watchHistoryRepository.findByUserIdOrderByLastWatchedAtDesc(user.getId()).isEmpty()) {
            return;
        }
        List<Movie> movies = movieRepository.findAll();
        if (movies.isEmpty()) {
            return;
        }
        Movie movie = movies.stream().filter(item -> item.getAccessLevel() == com.streaming.movieplatform.enums.AccessLevel.FREE).findFirst().orElse(movies.get(0));
        if (favoriteRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).isEmpty()) {
            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setMovie(movie);
            favoriteRepository.save(favorite);
        }

        List<Episode> episodes = episodeRepository.findByMovieIdOrderByEpisodeNumberAsc(movie.getId());
        if (!episodes.isEmpty() && watchHistoryRepository.findByUserIdOrderByLastWatchedAtDesc(user.getId()).isEmpty()) {
            com.streaming.movieplatform.entity.WatchHistory history = new com.streaming.movieplatform.entity.WatchHistory();
            history.setUser(user);
            history.setMovie(movie);
            history.setEpisode(episodes.get(0));
            history.setLastPositionSeconds(420);
            history.setCompleted(false);
            history.setLastWatchedAt(LocalDateTime.now().minusDays(1));
            history.setCreatedAt(LocalDateTime.now().minusDays(1));
            history.setUpdatedAt(LocalDateTime.now().minusDays(1));
            watchHistoryRepository.save(history);
        }

        if (commentRepository.findAll().isEmpty()) {
            Comment comment = new Comment();
            comment.setMovie(movie);
            comment.setUser(user);
            comment.setContent("Visual rất đẹp, nhịp phim tốt và giao diện player của demo cũng mượt.");
            commentRepository.save(comment);
        }

        if (ratingRepository.findByMovieId(movie.getId()).isEmpty()) {
            Rating rating = new Rating();
            rating.setMovie(movie);
            rating.setUser(user);
            rating.setStars(5);
            ratingRepository.save(rating);
            movie.setAverageRating(5.0);
            movieRepository.save(movie);
        }
    }
}
