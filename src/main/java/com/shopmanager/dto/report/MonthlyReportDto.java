package com.shopmanager.dto.report;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MonthlyReportDto {

    private String month;

    private Double totalRevenue;
    private Double totalSales;
    private Long totalRepairs;
    private Double averageDailySales;
    private Double gstCollected;
    private Double growth;

    private List<DailyData> dailyData;
    private List<TopCustomer> topCustomers;

    @Data
    @Builder
    public static class DailyData {
        private Integer day;
        private Double sales;
        private Long repairs;
    }


    @Data
    @Builder
    public static class TopCustomer {

        private Long customerId;   // needed

        private String name;

        private Double totalSpent; // rename from amount
    }
}