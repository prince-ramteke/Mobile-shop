package com.shopmanager.dto.report;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DailyReportDto {

    private String date;

    private Double totalRevenue;
    private Double totalSales;
    private Long totalRepairs;
    private Double gstCollected;

    private List<HourlyData> hourlyData;
    private List<TransactionRow> transactions;

    @Data
    @Builder
    public static class HourlyData {
        private String hour;
        private Double sales;
        private Long repairs;
    }

    @Data
    @Builder
    public static class TransactionRow {
        private Long id;
        private String type;
        private String customer;
        private Double amount;
        private String time;
    }
}