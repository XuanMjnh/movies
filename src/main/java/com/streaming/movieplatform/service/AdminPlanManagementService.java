package com.streaming.movieplatform.service;

import com.streaming.movieplatform.dto.AdminPlanRequest;
import com.streaming.movieplatform.entity.SubscriptionPlan;

import java.util.List;

public interface AdminPlanManagementService {
    List<SubscriptionPlan> getAllPlans();
    SubscriptionPlan getPlanById(Long planId);
    SubscriptionPlan savePlan(AdminPlanRequest request);
    void deletePlan(Long planId);
}
