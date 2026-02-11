package com.shopmanager.dashboard.service;

import com.shopmanager.dashboard.DashboardStatsResponse;
import com.shopmanager.dashboard.dto.DashboardStatsDto;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public interface DashboardService {
    DashboardStatsResponse getDashboardStats();
    Map<String, Object> getTodaySummary();
    Map<String, Object> getQuickStats();
    Map<String, Long> getPendingCounts();
    List<Map<String, Object>> getRevenueChartData(String period);
    List<Map<String, Object>> getRepairStatusDistribution();
}