package com.streaming.movieplatform.repository;

import com.streaming.movieplatform.entity.UserSubscription;
import com.streaming.movieplatform.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    Optional<UserSubscription> findFirstByUserIdAndStatusOrderByEndDateDesc(Long userId, SubscriptionStatus status);
    List<UserSubscription> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<UserSubscription> findTop20ByOrderByCreatedAtDesc();
}
