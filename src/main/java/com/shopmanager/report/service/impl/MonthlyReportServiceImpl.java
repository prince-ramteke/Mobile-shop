    package com.shopmanager.report.service.impl;

    import com.shopmanager.report.dto.MonthlyReportResponse;
    import com.shopmanager.report.repository.MonthlyReportRepository;
    import com.shopmanager.report.service.MonthlyReportService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;

    import java.math.BigDecimal;
    import java.time.LocalDate;
    import java.time.LocalDateTime;

    @Service
    @RequiredArgsConstructor
    public class MonthlyReportServiceImpl implements MonthlyReportService {

        private final MonthlyReportRepository reportRepository;

        @Override
        public MonthlyReportResponse getMonthlyReport(int year, int month) {

            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = startDate.plusMonths(1).minusDays(1).atTime(23, 59, 59);

            Object[] row = reportRepository
                    .getMonthlySummary(start, end)
                    .orElseThrow();

            return new MonthlyReportResponse(
                    year,
                    month,
                    (Long) row[0],
                    (BigDecimal) row[1],
                    (BigDecimal) row[2],
                    (BigDecimal) row[3],
                    (BigDecimal) row[4],
                    (BigDecimal) row[5]
            );
        }
    }