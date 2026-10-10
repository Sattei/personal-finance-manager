package com.syfe.personalfinancemanager.controller;

import com.syfe.personalfinancemanager.dto.report.MonthlyReportResponse;
import com.syfe.personalfinancemanager.dto.report.YearlyReportResponse;
import com.syfe.personalfinancemanager.service.ReportService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Handles monthly and yearly financial report endpoints
 * for authenticated users.
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Generates a monthly financial report for the authenticated user.
     *
     * @param year report year
     * @param month report month
     * @param authentication authenticated user
     * @return monthly financial report
     */
    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<MonthlyReportResponse> getMonthlyReport(
            @PathVariable int year,
            @PathVariable int month,
            Authentication authentication) {

        MonthlyReportResponse response =
                reportService.getMonthlyReport(
                        year,
                        month,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Generates a yearly financial report for the authenticated user.
     *
     * @param year report year
     * @param authentication authenticated user
     * @return yearly financial report
     */
    @GetMapping("/yearly/{year}")
    public ResponseEntity<YearlyReportResponse> getYearlyReport(
            @PathVariable int year,
            Authentication authentication) {

        YearlyReportResponse response =
                reportService.getYearlyReport(
                        year,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }
}