package com.syfe.personalfinancemanager.controller;

import com.syfe.personalfinancemanager.dto.report.MonthlyReportResponse;
import com.syfe.personalfinancemanager.dto.report.YearlyReportResponse;
import com.syfe.personalfinancemanager.exception.BadRequestException;
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

        validateYear(year);

        if (month < 1 || month > 12) {
            throw new BadRequestException(
                    "Month must be between 1 and 12"
            );
        }

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

        validateYear(year);

        YearlyReportResponse response =
                reportService.getYearlyReport(
                        year,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }

    private void validateYear(int year) {

        if (year < 1 || year > 9999) {
            throw new BadRequestException(
                    "Year must be between 1 and 9999"
            );
        }
    }
}