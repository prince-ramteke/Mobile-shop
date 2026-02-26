package com.shopmanager.service.impl;

import com.shopmanager.dto.report.DailyReportDto;
import com.shopmanager.dto.report.DashboardSummaryDto;
import com.shopmanager.dto.report.MonthlyReportDto;
import com.shopmanager.entity.MobileSale;
import com.shopmanager.entity.RepairJob;
import com.shopmanager.repository.MobileSaleRepository;
import com.shopmanager.repository.RepairJobRepository;
import com.shopmanager.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

import com.shopmanager.entity.Customer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final MobileSaleRepository mobileSaleRepository;
    private final RepairJobRepository repairJobRepository;

    // ================= DAILY REPORT =================

    @Override
    @Transactional(readOnly = true)
    public DailyReportDto getDailyReport(String dateStr) {

        LocalDate date = LocalDate.parse(dateStr);

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        List<MobileSale> sales = mobileSaleRepository.findByCreatedAtBetween(start, end);
        List<RepairJob> repairs = repairJobRepository.findByCreatedAtBetween(start, end);

        double totalSales = sales.stream()
                .mapToDouble(s -> s.getTotalAmount() != null ? s.getTotalAmount().doubleValue() : 0)                .sum();

        double totalRevenue = totalSales +
                repairs.stream()
                        .mapToDouble(r -> {
                            if (r.getFinalCost() != null)
                                return r.getFinalCost().doubleValue();
                            if (r.getAdvancePaid() != null)
                                return r.getAdvancePaid().doubleValue();
                            return 0.0;
                        })
                        .sum();

        long totalRepairs = repairs.size();

        // ===== Hourly Chart =====
        Map<Integer, Double> hourlySales = new HashMap<>();
        Map<Integer, Long> hourlyRepairs = new HashMap<>();

        for (RepairJob r : repairs) {
            if (r.getCreatedAt() == null) continue;
            int hour = r.getCreatedAt().getHour();
            hourlyRepairs.put(hour,
                    hourlyRepairs.getOrDefault(hour, 0L) + 1);
        }

        for (MobileSale s : sales) {
            if (s.getCreatedAt() == null) continue;
            int hour = s.getCreatedAt().getHour();
            hourlySales.put(hour,
                    hourlySales.getOrDefault(hour, 0.0) + s.getTotalAmount().doubleValue());
        }

        List<DailyReportDto.HourlyData> hourlyData = new ArrayList<>();
        for (int i = 9; i <= 20; i++) {
            hourlyData.add(DailyReportDto.HourlyData.builder()
                    .hour(i + ":00")
                    .sales(hourlySales.getOrDefault(i, 0.0))
                    .repairs(hourlyRepairs.getOrDefault(i, 0L))                    .build());
        }

        // ===== Transactions =====
        List<DailyReportDto.TransactionRow> transactions = new ArrayList<>();

// ---- Add Sales ----
        for (MobileSale s : sales) {
            transactions.add(DailyReportDto.TransactionRow.builder()
                    .id(s.getId())
                    .type("Sale")
                    .customer("Customer #" + s.getCustomerId())
                    .amount(s.getTotalAmount() != null ? s.getTotalAmount().doubleValue() : 0)
                    .time(
                            s.getCreatedAt() != null
                                    ? s.getCreatedAt().toLocalTime().toString()
                                    : "00:00"
                    )                    .build());
        }

// ---- Add Repairs ----
        for (RepairJob r : repairs) {

            double repairAmount = 0;

            if (r.getFinalCost() != null) {
                repairAmount = r.getFinalCost().doubleValue();
            } else if (r.getEstimatedCost() != null) {
                repairAmount = r.getEstimatedCost().doubleValue();
            }

            transactions.add(DailyReportDto.TransactionRow.builder()
                    .id(r.getId())
                    .type("Repair")
                    .customer(
                            r.getCustomer() != null
                                    ? r.getCustomer().getName()
                                    : "Walk-in Customer"
                    )
                    .amount(repairAmount)
                    .time(
                            r.getCreatedAt() != null
                                    ? r.getCreatedAt().toLocalTime().toString()
                                    : "00:00"
                    )                    .build());
        }

// ---- Sort by time ----
        transactions.sort(Comparator.comparing(DailyReportDto.TransactionRow::getTime));

        return DailyReportDto.builder()
                .date(dateStr)
                .totalRevenue(totalRevenue)
                .totalSales(totalSales)
                .totalRepairs(totalRepairs)
                .gstCollected(0.0)
                .hourlyData(hourlyData)
                .transactions(transactions)
                .build();
    }

    // ================= MONTHLY REPORT =================

    @Override
    @Transactional(readOnly = true)
    public MonthlyReportDto getMonthlyReport(int year, int month) {

        YearMonth ym = YearMonth.of(year, month);

        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59);

        List<MobileSale> sales = mobileSaleRepository.findByCreatedAtBetween(start, end);
        List<RepairJob> repairs = repairJobRepository.findByCreatedAtBetween(start, end);

        if (sales.isEmpty() && repairs.isEmpty()) {
            return MonthlyReportDto.builder()
                    .month(ym.toString())
                    .totalRevenue(0.0)
                    .totalSales(0.0)
                    .totalRepairs(0L)
                    .averageDailySales(0.0)
                    .gstCollected(0.0)
                    .growth(0.0)
                    .dailyData(Collections.emptyList())
                    .topCustomers(Collections.emptyList())
                    .build();
        }

        double totalSales = sales.stream()
                .mapToDouble(s -> s.getTotalAmount() != null ? s.getTotalAmount().doubleValue() : 0)                .sum();

        double totalRevenue = totalSales +
                repairs.stream()
                        .mapToDouble(r -> {
                            if (r.getFinalCost() != null)
                                return r.getFinalCost().doubleValue();
                            if (r.getAdvancePaid() != null)
                                return r.getAdvancePaid().doubleValue();
                            return 0.0;
                        })
                        .sum();

        long totalRepairs = repairs.size();

        int days = ym.lengthOfMonth();
        double avgDaily = days > 0 ? Math.round((totalSales / days) * 100.0) / 100.0 : 0;
        // ===== Daily Chart =====
        Map<Integer, Double> dailySales = new HashMap<>();
        Map<Integer, Long> dailyRepairs = new HashMap<>();

        for (RepairJob r : repairs) {
            if (r.getCreatedAt() == null) continue;
            int day = r.getCreatedAt().getDayOfMonth();
            dailyRepairs.put(day,
                    dailyRepairs.getOrDefault(day, 0L) + 1);
        }

        for (MobileSale s : sales) {
            if (s.getCreatedAt() == null) continue;
            int day = s.getCreatedAt().getDayOfMonth();
            dailySales.put(day,
                    dailySales.getOrDefault(day, 0.0) + s.getTotalAmount().doubleValue());
        }

        List<MonthlyReportDto.DailyData> dailyData = new ArrayList<>();

        for (int i = 1; i <= days; i++) {
            dailyData.add(MonthlyReportDto.DailyData.builder()
                    .day(i)
                    .sales(dailySales.getOrDefault(i, 0.0))
                    .repairs(dailyRepairs.getOrDefault(i, 0L))                    .build());
        }
        YearMonth prev = ym.minusMonths(1);

        LocalDateTime prevStart = prev.atDay(1).atStartOfDay();
        LocalDateTime prevEnd = prev.atEndOfMonth().atTime(23, 59, 59);

        double prevSales = mobileSaleRepository.findByCreatedAtBetween(prevStart, prevEnd)
                .stream()
                .mapToDouble(s -> s.getTotalAmount() != null ? s.getTotalAmount().doubleValue() : 0)
                .sum();

        double growth = prevSales > 0
                ? Math.round((((totalSales - prevSales) / prevSales) * 100) * 100.0) / 100.0
                : 0;

        // ===== TOP CUSTOMERS =====
        Map<Long, Double> customerTotals = new HashMap<>();

// Add Mobile Sales revenue per customer
        for (MobileSale s : sales) {
            if (s.getCustomerId() == null) continue;

            double amount = s.getTotalAmount() != null
                    ? s.getTotalAmount().doubleValue()
                    : 0;

            customerTotals.put(
                    s.getCustomerId(),
                    customerTotals.getOrDefault(s.getCustomerId(), 0.0) + amount
            );
        }

// Add Repair revenue per customer
        for (RepairJob r : repairs) {

            if (r.getCustomer() == null || r.getCustomer().getId() == null) continue;

            double repairAmount = 0;
            if (r.getFinalCost() != null) repairAmount = r.getFinalCost().doubleValue();
            else if (r.getAdvancePaid() != null) repairAmount = r.getAdvancePaid().doubleValue();

            Long customerId = r.getCustomer().getId();

            customerTotals.put(
                    customerId,
                    customerTotals.getOrDefault(customerId, 0.0) + repairAmount
            );
        }

// Convert to DTO list
        List<MonthlyReportDto.TopCustomer> topCustomers =
                customerTotals.entrySet()
                        .stream()
                        .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                        .limit(5)
                        .map(entry -> MonthlyReportDto.TopCustomer.builder()
                                .customerId(entry.getKey())
                                .name("Customer #" + entry.getKey())
                                .totalSpent(entry.getValue())
                                .build())
                        .collect(Collectors.toList());

        return MonthlyReportDto.builder()
                .month(ym.toString())
                .totalRevenue(totalRevenue)
                .totalSales(totalSales)
                .totalRepairs(totalRepairs)
                .averageDailySales(avgDaily)
                .gstCollected(0.0)
                .growth(growth)                .dailyData(dailyData)
                .topCustomers(topCustomers)                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryDto getDashboardSummary() {

        LocalDate today = LocalDate.now();

        LocalDateTime startDay = today.atStartOfDay();
        LocalDateTime endDay = today.atTime(23,59,59);

        // TODAY SALES + REPAIRS
        List<MobileSale> todaySalesList = mobileSaleRepository.findByCreatedAtBetween(startDay, endDay);
        List<RepairJob> todayRepairsList = repairJobRepository.findByCreatedAtBetween(startDay, endDay);

        double todaySales = todaySalesList.stream()
                .mapToDouble(s -> s.getTotalAmount() != null ? s.getTotalAmount().doubleValue() : 0)
                .sum();

        double todayRepairRevenue = todayRepairsList.stream()
                .mapToDouble(r -> {
                    if (r.getFinalCost() != null) return r.getFinalCost().doubleValue();
                    if (r.getAdvancePaid() != null) return r.getAdvancePaid().doubleValue();
                    return 0.0;
                }).sum();

        double todayRevenue = todaySales + todayRepairRevenue;

        long todayRepairs = todayRepairsList.size();

        // MONTH REVENUE
        YearMonth ym = YearMonth.now();
        LocalDateTime startMonth = ym.atDay(1).atStartOfDay();
        LocalDateTime endMonth = ym.atEndOfMonth().atTime(23,59,59);

        double monthSales = mobileSaleRepository.findByCreatedAtBetween(startMonth, endMonth)
                .stream()
                .mapToDouble(s -> s.getTotalAmount() != null ? s.getTotalAmount().doubleValue() : 0)
                .sum();

        double monthRepairs = repairJobRepository.findByCreatedAtBetween(startMonth, endMonth)
                .stream()
                .mapToDouble(r -> {
                    if (r.getFinalCost() != null) return r.getFinalCost().doubleValue();
                    if (r.getAdvancePaid() != null) return r.getAdvancePaid().doubleValue();
                    return 0.0;
                }).sum();

        double monthRevenue = monthSales + monthRepairs;

        // PENDING REPAIR AMOUNT
        double pendingAmount = repairJobRepository.findAll().stream()
                .mapToDouble(r -> r.getPendingAmount() != null ? r.getPendingAmount().doubleValue() : 0)
                .sum();

        // GROWTH
        YearMonth prev = ym.minusMonths(1);
        LocalDateTime prevStart = prev.atDay(1).atStartOfDay();
        LocalDateTime prevEnd = prev.atEndOfMonth().atTime(23,59,59);

        double prevMonthSales = mobileSaleRepository.findByCreatedAtBetween(prevStart, prevEnd)
                .stream()
                .mapToDouble(s -> s.getTotalAmount() != null ? s.getTotalAmount().doubleValue() : 0)
                .sum();

        double growth = prevMonthSales > 0
                ? Math.round(((monthSales - prevMonthSales) / prevMonthSales) * 100 * 100.0) / 100.0
                : 0;

        return DashboardSummaryDto.builder()
                .todayRevenue(todayRevenue)
                .todaySales(todaySales)
                .todayRepairs(todayRepairs)
                .monthRevenue(monthRevenue)
                .pendingRepairAmount(pendingAmount)
                .growth(growth)
                .build();
    }
}