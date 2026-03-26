package com.streaming.movieplatform.dto;

import java.math.BigDecimal;

public class AdminRevenueItem {

    private String periodLabel;
    private BigDecimal revenue;

    public AdminRevenueItem(String periodLabel, BigDecimal revenue) {
        this.periodLabel = periodLabel;
        this.revenue = revenue;
    }

    public String getPeriodLabel() {
        return periodLabel;
    }

    public void setPeriodLabel(String periodLabel) {
        this.periodLabel = periodLabel;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }
}
