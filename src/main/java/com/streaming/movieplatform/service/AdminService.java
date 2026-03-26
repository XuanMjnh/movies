package com.streaming.movieplatform.service;

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
import com.streaming.movieplatform.entity.SubscriptionPlan;
import com.streaming.movieplatform.entity.User;
import com.streaming.movieplatform.entity.Voucher;

import java.util.List;

public interface AdminService {
    AdminDashboardStats getDashboardStats();
    List<AdminRevenueItem> getDailyRevenueStats();
    List<AdminRevenueItem> getMonthlyRevenueStats();
    List<AdminChartItem> getMovieTypeStats();
    List<AdminChartItem> getTopViewedMovieStats();
    List<User> getAllUsers();
    User updateUser(AdminUserUpdateRequest request);
    List<Genre> getAllGenres();
    Genre saveGenre(AdminGenreRequest request);
    void deleteGenre(Long genreId);
    List<Banner> getAllBanners();
    Banner saveBanner(AdminBannerRequest request);
    void deleteBanner(Long bannerId);
    List<SubscriptionPlan> getAllPlans();
    SubscriptionPlan savePlan(AdminPlanRequest request);
    void deletePlan(Long planId);
    List<Voucher> getAllVouchers();
    Voucher saveVoucher(AdminVoucherRequest request);
    void deleteVoucher(Long voucherId);
}
