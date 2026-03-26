package com.streaming.movieplatform.service.impl;

import com.streaming.movieplatform.dto.ForgotPasswordRequest;
import com.streaming.movieplatform.dto.PasswordChangeRequest;
import com.streaming.movieplatform.dto.ProfileUpdateRequest;
import com.streaming.movieplatform.dto.RegisterRequest;
import com.streaming.movieplatform.entity.Role;
import com.streaming.movieplatform.entity.User;
import com.streaming.movieplatform.entity.UserSubscription;
import com.streaming.movieplatform.entity.Wallet;
import com.streaming.movieplatform.enums.AccessLevel;
import com.streaming.movieplatform.enums.RoleName;
import com.streaming.movieplatform.enums.SubscriptionStatus;
import com.streaming.movieplatform.exception.BusinessException;
import com.streaming.movieplatform.exception.ResourceNotFoundException;
import com.streaming.movieplatform.repository.RoleRepository;
import com.streaming.movieplatform.repository.UserRepository;
import com.streaming.movieplatform.repository.UserSubscriptionRepository;
import com.streaming.movieplatform.repository.WalletRepository;
import com.streaming.movieplatform.service.StorageService;
import com.streaming.movieplatform.service.UserService;
import com.streaming.movieplatform.util.CurrentUserUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final WalletRepository walletRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final PasswordEncoder passwordEncoder;
    private final StorageService storageService;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           WalletRepository walletRepository,
                           UserSubscriptionRepository userSubscriptionRepository,
                           PasswordEncoder passwordEncoder,
                           StorageService storageService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.walletRepository = walletRepository;
        this.userSubscriptionRepository = userSubscriptionRepository;
        this.passwordEncoder = passwordEncoder;
        this.storageService = storageService;
    }

    @Override
    public User register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("Email đã tồn tại trong hệ thống");
        }

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy role mặc định ROLE_USER"));

        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(email);
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.getRoles().add(userRole);

        User savedUser = userRepository.save(user);

        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        walletRepository.save(wallet);
        savedUser.setWallet(wallet);
        return savedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        String email = CurrentUserUtil.getCurrentEmail();
        if (email == null) {
            return null;
        }
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với id = " + id));
    }

    @Override
    public User updateProfile(ProfileUpdateRequest request) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException("Bạn cần đăng nhập để cập nhật hồ sơ");
        }
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        userRepository.findByEmail(email)
                .filter(found -> !found.getId().equals(currentUser.getId()))
                .ifPresent(found -> {
                    throw new BusinessException("Email này đã được sử dụng bởi tài khoản khác");
                });

        currentUser.setFullName(request.getFullName().trim());
        currentUser.setEmail(email);
        currentUser.setPhone(request.getPhone());
        currentUser.setAvatarUrl(storageService.store(request.getAvatarFile(), "avatars", currentUser.getAvatarUrl()));
        return userRepository.save(currentUser);
    }

    @Override
    public void changePassword(PasswordChangeRequest request) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException("Bạn cần đăng nhập để đổi mật khẩu");
        }
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            throw new BusinessException("Mật khẩu hiện tại không chính xác");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Mật khẩu xác nhận không khớp");
        }
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
    }

    @Override
    public void resetPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new BusinessException("Không tìm thấy tài khoản với email đã nhập"));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    @Override
    public AccessLevel getCurrentAccessLevel(User user) {
        UserSubscription subscription = getCurrentSubscription(user);
        return subscription == null ? AccessLevel.FREE : subscription.getPlan().getAccessLevel();
    }

    @Override
    public UserSubscription getCurrentSubscription(User user) {
        if (user == null) {
            return null;
        }
        UserSubscription subscription = userSubscriptionRepository
                .findFirstByUserIdAndStatusOrderByEndDateDesc(user.getId(), SubscriptionStatus.ACTIVE)
                .orElse(null);
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
    public long getRemainingSubscriptionDays(User user) {
        UserSubscription subscription = getCurrentSubscription(user);
        if (subscription == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), subscription.getEndDate()) + 1;
    }
}
