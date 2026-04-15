package com.streaming.movieplatform.service;

import com.streaming.movieplatform.dto.AdminChartItem;
import com.streaming.movieplatform.dto.AdminDashboardStats;
import com.streaming.movieplatform.dto.AdminRevenueItem;

import java.util.List;

public interface AdminDashboardService {
    AdminDashboardStats getDashboardStats();
    List<AdminRevenueItem> getDailyRevenueStats();
    List<AdminRevenueItem> getMonthlyRevenueStats();
    List<AdminChartItem> getMovieTypeStats();
    List<AdminChartItem> getTopViewedMovieStats();
}
