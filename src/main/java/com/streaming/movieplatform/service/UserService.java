package com.streaming.movieplatform.service;

import com.streaming.movieplatform.dto.PasswordChangeRequest;
import com.streaming.movieplatform.dto.ProfileUpdateRequest;
import com.streaming.movieplatform.dto.RegisterRequest;
import com.streaming.movieplatform.entity.User;
import com.streaming.movieplatform.entity.UserSubscription;
import com.streaming.movieplatform.enums.AccessLevel;

public interface UserService {
    User register(RegisterRequest request);
    User getCurrentUser();
    User getById(Long id);
    User updateProfile(ProfileUpdateRequest request);
    void changePassword(PasswordChangeRequest request);
    AccessLevel getCurrentAccessLevel(User user);
    UserSubscription getCurrentSubscription(User user);
    long getRemainingSubscriptionDays(User user);
}
