package com.streaming.movieplatform.service;

import com.streaming.movieplatform.entity.SubscriptionPlan;
import com.streaming.movieplatform.entity.User;
import com.streaming.movieplatform.entity.UserSubscription;

import java.util.List;

public interface SubscriptionService {
    List<SubscriptionPlan> getActivePlans();
    UserSubscription purchasePlan(User user, Long planId, String voucherCode);
    UserSubscription getCurrentSubscription(User user);
    List<UserSubscription> getSubscriptionHistory(User user);
    List<UserSubscription> getLatestSubscriptions();
    void refreshStatus(User user);
}
