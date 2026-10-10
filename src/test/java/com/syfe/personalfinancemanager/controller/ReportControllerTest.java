package com.syfe.personalfinancemanager.controller;

import com.syfe.personalfinancemanager.dto.report.MonthlyReportResponse;
import com.syfe.personalfinancemanager.dto.report.YearlyReportResponse;
import com.syfe.personalfinancemanager.service.ReportService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportControllerTest {

    @Mock
    private ReportService reportService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ReportController reportController;

    @Test
    void monthlyReportSuccess() {

        Map<String, BigDecimal> income =
                new LinkedHashMap<>();

        income.put(
                "Salary",
                new BigDecimal("5000")
        );

        Map<String, BigDecimal> expenses =
                new LinkedHashMap<>();

        expenses.put(
                "Food",
                new BigDecimal("1000")
        );

        MonthlyReportResponse serviceResponse =
                new MonthlyReportResponse(
                        10,
                        2026,
                        income,
                        expenses,
                        new BigDecimal("4000")
                );

        when(authentication.getName())
                .thenReturn("user@example.com");

        when(reportService.getMonthlyReport(
                2026,
                10,
                "user@example.com"
        )).thenReturn(serviceResponse);

        ResponseEntity<MonthlyReportResponse> response =
                reportController.getMonthlyReport(
                        2026,
                        10,
                        authentication
                );

        assertEquals(200, response.getStatusCode().value());

        assertEquals(
                new BigDecimal("4000"),
                response.getBody().getNetSavings()
        );
    }

    @Test
    void yearlyReportSuccess() {

        Map<String, BigDecimal> income =
                new LinkedHashMap<>();

        Map<String, BigDecimal> expenses =
                new LinkedHashMap<>();

        YearlyReportResponse serviceResponse =
                new YearlyReportResponse(
                        2026,
                        income,
                        expenses,
                        new BigDecimal("10000")
                );

        when(authentication.getName())
                .thenReturn("user@example.com");

        when(reportService.getYearlyReport(
                2026,
                "user@example.com"
        )).thenReturn(serviceResponse);

        ResponseEntity<YearlyReportResponse> response =
                reportController.getYearlyReport(
                        2026,
                        authentication
                );

        assertEquals(200, response.getStatusCode().value());

        assertEquals(
                2026,
                response.getBody().getYear()
        );
    }
}