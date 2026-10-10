package com.syfe.personalfinancemanager.dto.report;

import java.math.BigDecimal;
import java.util.Map;

public class YearlyReportResponse {

    private int year;
    private Map<String, BigDecimal> totalIncome;
    private Map<String, BigDecimal> totalExpenses;
    private BigDecimal netSavings;

    public YearlyReportResponse(
            int year,
            Map<String, BigDecimal> totalIncome,
            Map<String, BigDecimal> totalExpenses,
            BigDecimal netSavings) {

        this.year = year;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.netSavings = netSavings;
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