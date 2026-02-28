package com.shopmanager.util;

import com.shopmanager.dto.report.MonthlyReportDto;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class ChartGenerator {

    public static String generateMonthlyRevenueChart(MonthlyReportDto report) {
        try {

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            report.getDailyData().forEach(d ->
                    dataset.addValue(d.getSales(), "Sales", String.valueOf(d.getDay()))
            );

            JFreeChart chart = ChartFactory.createLineChart(
                    "Monthly Sales Trend",
                    "Day",
                    "Revenue",
                    dataset
            );

            chart.setBackgroundPaint(Color.WHITE);

            BufferedImage image = chart.createBufferedImage(800, 400);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);

            return "data:image/png;base64," +
                    Base64.getEncoder().encodeToString(baos.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Chart generation failed", e);
        }
    }
}