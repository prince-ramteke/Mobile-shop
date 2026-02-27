package com.shopmanager.dto.report;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardSummaryDto {

    private Double todayRevenue;
    private Double todaySales;
    private Long todayRepairs;

    private Double monthRevenue;

    private Double pendingRepairAmount;

    private Double growth;
}