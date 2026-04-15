package com.streaming.movieplatform.service.impl;

import com.streaming.movieplatform.dto.AdminPlanRequest;
import com.streaming.movieplatform.entity.SubscriptionPlan;
import com.streaming.movieplatform.exception.ResourceNotFoundException;
import com.streaming.movieplatform.repository.SubscriptionPlanRepository;
import com.streaming.movieplatform.service.AdminPlanManagementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminPlanManagementServiceImpl implements AdminPlanManagementService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public AdminPlanManagementServiceImpl(SubscriptionPlanRepository subscriptionPlanRepository) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
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
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay goi thanh vien"));
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
}
