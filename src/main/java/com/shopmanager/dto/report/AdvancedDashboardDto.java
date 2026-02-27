package com.shopmanager.dto.report;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AdvancedDashboardDto {

    private List<RevenueTrend> last30DaysRevenue;
    private List<MonthlyTrend> last6MonthsRevenue;
    private List<RepairTrend> last30DaysRepairs;

    @Data
    @Builder
    public static class RevenueTrend {
        private String date;
        private Double revenue;
    }

    @Data
    @Builder
    public static class MonthlyTrend {
        private String month;
        private Double revenue;
    }

    @Data
    @Builder
    public static class RepairTrend {
        private String date;
        private Long repairs;
    }
}