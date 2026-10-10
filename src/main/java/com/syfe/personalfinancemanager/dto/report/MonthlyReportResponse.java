package com.syfe.personalfinancemanager.dto.report;

import java.math.BigDecimal;
import java.util.Map;

public class MonthlyReportResponse {

    private int month;
    private int year;
    private Map<String, BigDecimal> totalIncome;
    private Map<String, BigDecimal> totalExpenses;
    private BigDecimal netSavings;

    public MonthlyReportResponse(
            int month,
            int year,
            Map<String, BigDecimal> totalIncome,
            Map<String, BigDecimal> totalExpenses,
            BigDecimal netSavings) {

        this.month = month;
        this.year = year;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.netSavings = netSavings;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public Map<String, BigDecimal> getTotalIncome() {
        return totalIncome;
    }

    public Map<String, BigDecimal> getTotalExpenses() {
        return totalExpenses;
    }

    public BigDecimal getNetSavings() {
        return netSavings;
    }
}