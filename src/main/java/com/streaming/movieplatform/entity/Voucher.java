package com.streaming.movieplatform.entity;

import com.streaming.movieplatform.enums.VoucherAudienceMatchMode;
import com.streaming.movieplatform.enums.VoucherDiscountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vouchers")
public class Voucher extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VoucherDiscountType discountType;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal discountValue;

    @Column(precision = 15, scale = 2)
    private BigDecimal maxDiscountAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal minOrderAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer quantity = 0;

    @Column(nullable = false)
    private Integer usedCount = 0;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "auto_display_enabled", nullable = false)
    private boolean autoDisplayEnabled = false;

    @Column(name = "min_total_spent_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal minTotalSpentAmount = BigDecimal.ZERO;

    @Column(name = "min_account_age_days", nullable = false)
    private Integer minAccountAgeDays = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "audience_match_mode", nullable = false, length = 10)
    private VoucherAudienceMatchMode audienceMatchMode = VoucherAudienceMatchMode.ALL;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public VoucherDiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(VoucherDiscountType discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public BigDecimal getMaxDiscountAmount() {
        return maxDiscountAmount;
    }

    public void setMaxDiscountAmount(BigDecimal maxDiscountAmount) {
        this.maxDiscountAmount = maxDiscountAmount;
    }

    public BigDecimal getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(BigDecimal minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isAutoDisplayEnabled() {
        return autoDisplayEnabled;
    }

    public void setAutoDisplayEnabled(boolean autoDisplayEnabled) {
        this.autoDisplayEnabled = autoDisplayEnabled;
    }

    public BigDecimal getMinTotalSpentAmount() {
        return minTotalSpentAmount;
    }

    public void setMinTotalSpentAmount(BigDecimal minTotalSpentAmount) {
        this.minTotalSpentAmount = minTotalSpentAmount;
    }

    public Integer getMinAccountAgeDays() {
        return minAccountAgeDays;
    }

    public void setMinAccountAgeDays(Integer minAccountAgeDays) {
        this.minAccountAgeDays = minAccountAgeDays;
    }

    public VoucherAudienceMatchMode getAudienceMatchMode() {
        return audienceMatchMode;
    }

    public void setAudienceMatchMode(VoucherAudienceMatchMode audienceMatchMode) {
        this.audienceMatchMode = audienceMatchMode;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public void setEndAt(LocalDateTime endAt) {
        this.endAt = endAt;
    }
}
