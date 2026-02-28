package com.shopmanager.service.impl;

import com.shopmanager.dto.report.AdvancedDashboardDto;
import com.shopmanager.dto.report.DailyReportDto;
import com.shopmanager.dto.report.DashboardSummaryDto;
import com.shopmanager.dto.report.MonthlyReportDto;
import com.shopmanager.entity.MobileSale;
import com.shopmanager.entity.RepairJob;
import com.shopmanager.entity.Sale;
import com.shopmanager.repository.CustomerRepository;
import com.shopmanager.repository.MobileSaleRepository;
import com.shopmanager.repository.RepairJobRepository;
import com.shopmanager.repository.SaleRepository;
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

    private final CustomerRepository customerRepository;
    private final SaleRepository saleRepository;

    // ================= DAILY REPORT =================

    @Override
    @Transactional(readOnly = true)
    public DailyReportDto getDailyReport(String dateStr) {

        LocalDate date = LocalDate.parse(dateStr);

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        List<MobileSale> sales = mobileSaleRepository.findByCreatedAtBetween(start, end);
        List<Sale> normalSales =
                saleRepository.findBySaleDateBetween(date, date);        // ===== Load Customer Names for Sales =====
        List<Long> customerIds = sales.stream()
                .map(MobileSale::getCustomerId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, String> customerNameMap =
                customerRepository.findByIdIn(customerIds)
                        .stream()
                        .collect(Collectors.toMap(
                                Customer::getId,
                                Customer::getName
                        ));
        List<RepairJob> repairs = repairJobRepository.findByCreatedAtBetween(start, end);

        double mobileSalesTotal = sales.stream()
                .mapToDouble(s -> s.getGrandTotal() != null ? s.getGrandTotal().doubleValue() : 0)
                .sum();

        double normalSalesTotal = normalSales.stream()
                .mapToDouble(s -> s.getGrandTotal() != null ? s.getGrandTotal().doubleValue() : 0)
                .sum();

        double totalSales = mobileSalesTotal + normalSalesTotal;

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
                    hourlySales.getOrDefault(hour, 0.0)
                            + (s.getGrandTotal() != null ? s.getGrandTotal().doubleValue() : 0));
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
                    .customer(
                            customerNameMap.getOrDefault(
                                    s.getCustomerId(),
                                    "Walk-in Customer"
                            )
                    )                    .amount(s.getGrandTotal() != null ? s.getGrandTotal().doubleValue() : 0)
                    .time(
                            s.getCreatedAt() != null
                                    ? s.getCreatedAt().toLocalTime().toString()
                                    : "00:00"
                    )                    .build());
        }
        // ---- Add Normal Sales ----
        for (Sale s : normalSales) {
            transactions.add(DailyReportDto.TransactionRow.builder()
                    .id(s.getId())
                    .type("Sale")
                    .customer(
                            s.getCustomer() != null
                                    ? s.getCustomer().getName()
                                    : "Walk-in Customer"
                    )
                    .amount(
                            s.getGrandTotal() != null
                                    ? s.getGrandTotal().doubleValue()
                                    : 0
                    )
                    .time(
                            s.getCreatedAt() != null
                                    ? s.getCreatedAt().toLocalTime().toString()
                                    : "00:00"
                    )
                    .build());
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

        List<Sale> normalSales =
                saleRepository.findBySaleDateBetween(
                        ym.atDay(1),
                        ym.atEndOfMonth()
                );        // ===== Load Customer Names for Sales =====
        List<Long> customerIds = sales.stream()
                .map(MobileSale::getCustomerId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, String> customerNameMap =
                customerRepository.findByIdIn(customerIds)
                        .stream()
                        .collect(Collectors.toMap(
                                Customer::getId,
                                Customer::getName
                        ));
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

        double mobileSalesTotal = sales.stream()
                .mapToDouble(s -> s.getGrandTotal() != null ? s.getGrandTotal().doubleValue() : 0)
                .sum();

        double normalSalesTotal = normalSales.stream()
                .mapToDouble(s -> s.getGrandTotal() != null ? s.getGrandTotal().doubleValue() : 0)
                .sum();

        double totalSales = mobileSalesTotal + normalSalesTotal;

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
                    dailySales.getOrDefault(day, 0.0)
                            + (s.getGrandTotal() != null
                            ? s.getGrandTotal().doubleValue()
                            : 0));
        }

        for (Sale s : normalSales) {
            if (s.getSaleDate() == null) continue;
            int day = s.getSaleDate().getDayOfMonth();
            dailySales.put(day,
                    dailySales.getOrDefault(day, 0.0)
                            + (s.getGrandTotal() != null
                            ? s.getGrandTotal().doubleValue()
                            : 0));
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

        double prevMobileSales = mobileSaleRepository
                .findByCreatedAtBetween(prevStart, prevEnd)
                .stream()
                .mapToDouble(s -> s.getGrandTotal() != null ? s.getGrandTotal().doubleValue() : 0)
                .sum();

        double prevNormalSales = saleRepository
                .findBySaleDateBetween(prev.atDay(1), prev.atEndOfMonth())
                .stream()
                .mapToDouble(s -> s.getGrandTotal() != null ? s.getGrandTotal().doubleValue() : 0)
                .sum();

        double prevSales = prevMobileSales + prevNormalSales;

        double growth = prevSales > 0
                ? Math.round((((totalSales - prevSales) / prevSales) * 100) * 100.0) / 100.0
                : 0;

        // ===== TOP CUSTOMERS =====
        Map<Long, Double> customerTotals = new HashMap<>();

// Add Mobile Sales revenue per customer
        for (MobileSale s : sales) {
            if (s.getCustomerId() == null) continue;

            double amount = s.getGrandTotal() != null
                    ? s.getGrandTotal().doubleValue()
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
        // ===== Load Real Customer Names =====
        List<Long> topCustomerIds = customerTotals.keySet().stream().toList();

        Map<Long, String> monthlyCustomerNameMap =
                customerRepository.findByIdIn(topCustomerIds)
                        .stream()
                        .collect(Collectors.toMap(
                                Customer::getId,
                                Customer::getName
                        ));
        List<MonthlyReportDto.TopCustomer> topCustomers =
                customerTotals.entrySet()
                        .stream()
                        .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                        .limit(5)
                        .map(entry -> MonthlyReportDto.TopCustomer.builder()
                                .customerId(entry.getKey())
                                .name(
                                        monthlyCustomerNameMap.getOrDefault(
                                                entry.getKey(),
                                                "Walk-in Customer"
                                        )
                                )                                .totalSpent(entry.getValue())
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
        List<Sale> todayNormalSalesList =
                saleRepository.findBySaleDateBetween(today, today);

        double mobileSalesToday = todaySalesList.stream()
                .mapToDouble(s -> s.getGrandTotal() != null ? s.getGrandTotal().doubleValue() : 0)
                .sum();

        double normalSalesToday = todayNormalSalesList.stream()
                .mapToDouble(s -> s.getGrandTotal() != null ? s.getGrandTotal().doubleValue() : 0)
                .sum();

        double todaySales = mobileSalesToday + normalSalesToday;

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

        double mobileMonthSales = mobileSaleRepository.findByCreatedAtBetween(startMonth, endMonth)
                .stream()
                .mapToDouble(s -> s.getGrandTotal() != null ? s.getGrandTotal().doubleValue() : 0)
                .sum();

        double normalMonthSales = saleRepository
                .findBySaleDateBetween(ym.atDay(1), ym.atEndOfMonth())
                .stream()
                .mapToDouble(s -> s.getGrandTotal() != null ? s.getGrandTotal().doubleValue() : 0)
                .sum();

        double monthSales = mobileMonthSales + normalMonthSales;

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

        double prevMobileSales = mobileSaleRepository
                .findByCreatedAtBetween(prevStart, prevEnd)
                .stream()
                .mapToDouble(s -> s.getGrandTotal() != null ? s.getGrandTotal().doubleValue() : 0)
                .sum();

        double prevNormalSales = saleRepository
                .findBySaleDateBetween(prev.atDay(1), prev.atEndOfMonth())
                .stream()
                .mapToDouble(s -> s.getGrandTotal() != null ? s.getGrandTotal().doubleValue() : 0)
                .sum();

        double prevTotalSales = prevMobileSales + prevNormalSales;

        double growth = prevTotalSales > 0
                ? Math.round(((monthSales - prevTotalSales) / prevTotalSales) * 100 * 100.0) / 100.0
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

    @Override
    @Transactional(readOnly = true)
    public AdvancedDashboardDto getAdvancedDashboard() {

        LocalDate today = LocalDate.now();
        LocalDate start30 = today.minusDays(29);

        // ===== Last 30 Days Revenue =====
        List<AdvancedDashboardDto.RevenueTrend> revenueTrend = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            LocalDate date = start30.plusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(23, 59, 59);

            double mobileSales = mobileSaleRepository.findByCreatedAtBetween(start, end)
                    .stream()
                    .mapToDouble(s -> s.getGrandTotal() != null ? s.getGrandTotal().doubleValue() : 0)
                    .sum();

            double normalSales = saleRepository
                    .findBySaleDateBetween(date, date)
                    .stream()
                    .mapToDouble(s -> s.getGrandTotal() != null ? s.getGrandTotal().doubleValue() : 0)
                    .sum();

            double sales = mobileSales + normalSales;

            double repairs = repairJobRepository.findByCreatedAtBetween(start, end)
                    .stream()
                    .mapToDouble(r -> {
                        if (r.getFinalCost() != null) return r.getFinalCost().doubleValue();
                        if (r.getAdvancePaid() != null) return r.getAdvancePaid().doubleValue();
                        return 0.0;
                    }).sum();

            revenueTrend.add(
                    AdvancedDashboardDto.RevenueTrend.builder()
                            .date(date.toString())
                            .revenue(sales + repairs)
                            .build()
            );
        }

        // ===== Last 6 Months Revenue =====
        List<AdvancedDashboardDto.MonthlyTrend> monthlyTrend = new ArrayList<>();

        for (int i = 5; i >= 0; i--) {
            YearMonth ym = YearMonth.now().minusMonths(i);
            LocalDateTime start = ym.atDay(1).atStartOfDay();
            LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59);

            double mobileSales = mobileSaleRepository.findByCreatedAtBetween(start, end)
                    .stream()
                    .mapToDouble(s -> s.getGrandTotal() != null ? s.getGrandTotal().doubleValue() : 0)
                    .sum();

            double normalSales = saleRepository
                    .findBySaleDateBetween(ym.atDay(1), ym.atEndOfMonth())
                    .stream()
                    .mapToDouble(s -> s.getGrandTotal() != null ? s.getGrandTotal().doubleValue() : 0)
                    .sum();

            double sales = mobileSales + normalSales;

            double repairs = repairJobRepository.findByCreatedAtBetween(start, end)
                    .stream()
                    .mapToDouble(r -> {
                        if (r.getFinalCost() != null) return r.getFinalCost().doubleValue();
                        if (r.getAdvancePaid() != null) return r.getAdvancePaid().doubleValue();
                        return 0.0;
                    }).sum();

            monthlyTrend.add(
                    AdvancedDashboardDto.MonthlyTrend.builder()
                            .month(ym.toString())
                            .revenue(sales + repairs)
                            .build()
            );
        }

        // ===== Last 30 Days Repair Count =====
        List<AdvancedDashboardDto.RepairTrend> repairTrend = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            LocalDate date = start30.plusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(23, 59, 59);

            long repairs = repairJobRepository.findByCreatedAtBetween(start, end).size();

            repairTrend.add(
                    AdvancedDashboardDto.RepairTrend.builder()
                            .date(date.toString())
                            .repairs(repairs)
                            .build()
            );
        }

        return AdvancedDashboardDto.builder()
                .last30DaysRevenue(revenueTrend)
                .last6MonthsRevenue(monthlyTrend)
                .last30DaysRepairs(repairTrend)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AdvancedDashboardDto getAdvancedDashboardOptimized() {

        LocalDate today = LocalDate.now();
        LocalDate start30 = today.minusDays(29);

        LocalDateTime start = start30.atStartOfDay();
        LocalDateTime end = today.atTime(23,59,59);

        Map<LocalDate, Double> revenueMap = new HashMap<>();
        Map<LocalDate, Long> repairCountMap = new HashMap<>();

        // ===== DAILY SALES =====
        List<Object[]> salesData =
                mobileSaleRepository.getDailySalesBetween(start, end);

        List<Object[]> normalSalesData =
                saleRepository.getDailyNormalSalesBetween(start.toLocalDate(), end.toLocalDate());

        // ===== MOBILE SALES =====
        for (Object[] row : salesData) {
            LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
            Double total = row[1] != null ? ((Number) row[1]).doubleValue() : 0;

            revenueMap.put(
                    date,
                    revenueMap.getOrDefault(date, 0.0) + total
            );
        }

// ===== NORMAL SALES =====
        for (Object[] row : normalSalesData) {
            LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
            Double total = row[1] != null ? ((Number) row[1]).doubleValue() : 0;

            revenueMap.put(
                    date,
                    revenueMap.getOrDefault(date, 0.0) + total
            );
        }

        // ===== DAILY REPAIRS =====
        List<Object[]> repairData =
                repairJobRepository.getDailyRepairStatsBetween(start, end);

        for (Object[] row : repairData) {
            LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
            Long count = row[1] != null ? ((Number) row[1]).longValue() : 0;
            Double amount = row[2] != null ? ((Number) row[2]).doubleValue() : 0;

            repairCountMap.put(date, count);
            revenueMap.put(date,
                    revenueMap.getOrDefault(date, 0.0) + amount);
        }

        List<AdvancedDashboardDto.RevenueTrend> revenueTrend = new ArrayList<>();
        List<AdvancedDashboardDto.RepairTrend> repairTrend = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            LocalDate date = start30.plusDays(i);

            revenueTrend.add(
                    AdvancedDashboardDto.RevenueTrend.builder()
                            .date(date.toString())
                            .revenue(revenueMap.getOrDefault(date, 0.0))
                            .build()
            );

            repairTrend.add(
                    AdvancedDashboardDto.RepairTrend.builder()
                            .date(date.toString())
                            .repairs(repairCountMap.getOrDefault(date, 0L))
                            .build()
            );
        }

        // ===== LAST 6 MONTHS =====

        YearMonth currentMonth = YearMonth.now();
        YearMonth startMonth = currentMonth.minusMonths(5);

        LocalDateTime monthStart = startMonth.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = currentMonth.atEndOfMonth().atTime(23,59,59);

        Map<String, Double> monthlyRevenueMap = new HashMap<>();

        // SALES
        List<Object[]> monthlySales =
                mobileSaleRepository.getMonthlySalesBetween(monthStart, monthEnd);

        for (Object[] row : monthlySales) {
            String month = (String) row[0];
            Double total = row[1] != null ? ((Number) row[1]).doubleValue() : 0;
            monthlyRevenueMap.put(month, total);
        }

        // REPAIRS
        List<Object[]> monthlyRepairs =
                repairJobRepository.getMonthlyRepairRevenueBetween(monthStart, monthEnd);

        for (Object[] row : monthlyRepairs) {
            String month = (String) row[0];
            Double total = row[1] != null ? ((Number) row[1]).doubleValue() : 0;

            monthlyRevenueMap.put(
                    month,
                    monthlyRevenueMap.getOrDefault(month, 0.0) + total
            );
        }

        List<AdvancedDashboardDto.MonthlyTrend> monthlyTrend = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            YearMonth ym = startMonth.plusMonths(i);
            String monthKey = ym.toString();

            monthlyTrend.add(
                    AdvancedDashboardDto.MonthlyTrend.builder()
                            .month(monthKey)
                            .revenue(monthlyRevenueMap.getOrDefault(monthKey, 0.0))
                            .build()
            );
        }

        return AdvancedDashboardDto.builder()
                .last30DaysRevenue(revenueTrend)
                .last30DaysRepairs(repairTrend)
                .last6MonthsRevenue(monthlyTrend)
                .build();
    }
}