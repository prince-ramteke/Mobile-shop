package com.shopmanager.dashboard.service.impl;

import com.shopmanager.dashboard.DashboardStatsResponse;
import com.shopmanager.dashboard.service.DashboardService;
import com.shopmanager.entity.enums.DueStatus;
import com.shopmanager.entity.enums.RepairStatus;
import com.shopmanager.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final SaleRepository saleRepository;
    private final RepairJobRepository repairJobRepository;
    private final CustomerRepository customerRepository;
    private final DueEntryRepository dueEntryRepository;

    @Override
    public DashboardStatsResponse getDashboardStats() {
        LocalDate today = LocalDate.now();

        BigDecimal todaySales = saleRepository.sumTodaySales(today);
        Long todayRepairs = repairJobRepository.countByCreatedAtDate(today);
        if (todayRepairs == null) {
            todayRepairs = repairJobRepository.countTodayJobs(today);
        }
        long totalCustomers = customerRepository.count();
        BigDecimal pendingDues = dueEntryRepository.sumPendingAmount();

        // Calculate growth (compare with previous period)
        BigDecimal yesterdaySales = saleRepository.sumTodaySales(today.minusDays(1));
        double salesGrowth = yesterdaySales != null && yesterdaySales.compareTo(BigDecimal.ZERO) > 0
                ? todaySales.subtract(yesterdaySales).multiply(BigDecimal.valueOf(100))
                .divide(yesterdaySales, 2, BigDecimal.ROUND_HALF_UP).doubleValue()
                : 0.0;

        return DashboardStatsResponse.builder()
                .todaySales(todaySales != null ? todaySales : BigDecimal.ZERO)
                .todayRepairs(todayRepairs != null ? todayRepairs.intValue() : 0)
                .totalCustomers(totalCustomers)
                .pendingDues(pendingDues != null ? pendingDues : BigDecimal.ZERO)
                .salesGrowth(salesGrowth)
                .repairsGrowth(0.0) // Calculate similarly if needed
                .repairStats(getRepairStats())
                .build();
    }

    private DashboardStatsResponse.RepairStats getRepairStats() {
        long pending = repairJobRepository.countByStatus(RepairStatus.PENDING);
        long inProgress = repairJobRepository.countByStatus(RepairStatus.IN_PROGRESS);
        long completed = repairJobRepository.countByStatus(RepairStatus.COMPLETED);

        return DashboardStatsResponse.RepairStats.builder()
                .pending((int) pending)
                .inProgress((int) inProgress)
                .completed((int) completed)
                .build();
    }

    @Override
    public Map<String, Object> getTodaySummary() {
        LocalDate today = LocalDate.now();
        Map<String, Object> summary = new HashMap<>();

        Long salesCount = saleRepository.countTodayInvoices(today);
        BigDecimal revenue = saleRepository.sumTodaySales(today);
        Long repairsCount = repairJobRepository.countByCreatedAtDate(today);
        if (repairsCount == null) {
            repairsCount = repairJobRepository.countTodayJobs(today);
        }

        summary.put("salesCount", salesCount != null ? salesCount : 0L);
        summary.put("revenue", revenue != null ? revenue : BigDecimal.ZERO);
        summary.put("repairsCount", repairsCount != null ? repairsCount : 0L);

        return summary;
    }

    @Override
    public Map<String, Object> getQuickStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalCustomers", customerRepository.count());
        stats.put("totalSales", saleRepository.count());
        stats.put("totalRepairs", repairJobRepository.count());
        stats.put("pendingDues", dueEntryRepository.sumPendingAmount());

        return stats;
    }

    @Override
    public Map<String, Long> getPendingCounts() {
        Map<String, Long> counts = new HashMap<>();

        counts.put("sales", saleRepository.countByPendingAmountGreaterThan(BigDecimal.ZERO));
        counts.put("repairs", repairJobRepository.countByPendingAmountGreaterThan(BigDecimal.ZERO));

        return counts;
    }

    @Override
    public List<Map<String, Object>> getRevenueChartData(String period) {
        List<Map<String, Object>> data = new ArrayList<>();
        LocalDate end = LocalDate.now();
        LocalDate start;

        switch (period.toLowerCase()) {
            case "week":
                start = end.minusDays(6);
                break;
            case "month":
                start = end.minusDays(29);
                break;
            default:
                start = end.minusDays(6);
        }

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("name", date.getDayOfWeek().toString().substring(0, 3));
            BigDecimal sales = saleRepository.sumTodaySales(date);
            dayData.put("value", sales != null ? sales : BigDecimal.ZERO);
            data.add(dayData);
        }

        return data;
    }

    @Override
    public List<Map<String, Object>> getRepairStatusDistribution() {
        List<Map<String, Object>> data = new ArrayList<>();

        for (RepairStatus status : RepairStatus.values()) {
            Map<String, Object> statusData = new HashMap<>();
            statusData.put("name", status.name());
            statusData.put("value", repairJobRepository.countByStatus(status));
            data.add(statusData);
        }

        return data;
    }
}