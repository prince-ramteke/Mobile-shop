package com.shopmanager.dashboard;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DashboardStatsResponse {
    private BigDecimal todaySales;
    private int todayRepairs;
    private long totalCustomers;
    private BigDecimal pendingDues;
    private double salesGrowth;
    private double repairsGrowth;
    private RepairStats repairStats;

    @Data
    @Builder
    public static class RepairStats {
        private int pending;
        private int inProgress;
        private int completed;
    }
}