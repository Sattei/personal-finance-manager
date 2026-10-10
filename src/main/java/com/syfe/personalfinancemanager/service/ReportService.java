package com.syfe.personalfinancemanager.service;

import com.syfe.personalfinancemanager.dto.report.MonthlyReportResponse;
import com.syfe.personalfinancemanager.dto.report.YearlyReportResponse;
import com.syfe.personalfinancemanager.entity.Transaction;
import com.syfe.personalfinancemanager.entity.User;
import com.syfe.personalfinancemanager.exception.BadRequestException;
import com.syfe.personalfinancemanager.exception.ResourceNotFoundException;
import com.syfe.personalfinancemanager.repository.TransactionRepository;
import com.syfe.personalfinancemanager.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Aggregates user transactions into monthly and yearly financial reports.
 */
@Service
public class ReportService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public ReportService(
            TransactionRepository transactionRepository,
            UserRepository userRepository) {

        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Calculates income, expenses, and net savings for one calendar month.
     *
     * @param year report year
     * @param month report month from 1 through 12
     * @param username username of the report owner
     * @return the monthly report grouped by category
     * @throws BadRequestException if the year or month is invalid
     */
    public MonthlyReportResponse getMonthlyReport(
            int year,
            int month,
            String username) {

        if (year < 1) {
            throw new BadRequestException("Invalid year");
        }

        if (month < 1 || month > 12) {
            throw new BadRequestException("Invalid month");
        }

        User user = getUser(username);

        LocalDate startDate =
                LocalDate.of(year, month, 1);

        LocalDate endDate =
                startDate.withDayOfMonth(
                        startDate.lengthOfMonth()
                );

        List<Transaction> transactions =
                transactionRepository.findTransactions(
                        user,
                        startDate,
                        endDate,
                        null,
                        null
                );

        ReportData data =
                calculateReport(transactions);

        return new MonthlyReportResponse(
                month,
                year,
                data.totalIncome,
                data.totalExpenses,
                data.netSavings
        );
    }

    /**
     * Calculates income, expenses, and net savings for one calendar year.
     *
     * @param year report year
     * @param username username of the report owner
     * @return the yearly report grouped by category
     * @throws BadRequestException if the year is invalid
     */
    public YearlyReportResponse getYearlyReport(
            int year,
            String username) {

        if (year < 1) {
            throw new BadRequestException("Invalid year");
        }

        User user = getUser(username);

        LocalDate startDate =
                LocalDate.of(year, 1, 1);

        LocalDate endDate =
                LocalDate.of(year, 12, 31);

        List<Transaction> transactions =
                transactionRepository.findTransactions(
                        user,
                        startDate,
                        endDate,
                        null,
                        null
                );

        ReportData data =
                calculateReport(transactions);

        return new YearlyReportResponse(
                year,
                data.totalIncome,
                data.totalExpenses,
                data.netSavings
        );
    }

    private ReportData calculateReport(
            List<Transaction> transactions) {

        Map<String, BigDecimal> totalIncome =
                new LinkedHashMap<>();

        Map<String, BigDecimal> totalExpenses =
                new LinkedHashMap<>();

        BigDecimal incomeSum =
                BigDecimal.ZERO;

        BigDecimal expenseSum =
                BigDecimal.ZERO;

        for (Transaction transaction : transactions) {

            String categoryName =
                    transaction.getCategory().getName();

            String type =
                    transaction.getCategory().getType();

            BigDecimal amount =
                    transaction.getAmount();

            if (type.equals("INCOME")) {

                totalIncome.merge(
                        categoryName,
                        amount,
                        BigDecimal::add
                );

                incomeSum =
                        incomeSum.add(amount);

            } else if (type.equals("EXPENSE")) {

                totalExpenses.merge(
                        categoryName,
                        amount,
                        BigDecimal::add
                );

                expenseSum =
                        expenseSum.add(amount);
            }
        }

        BigDecimal netSavings =
                incomeSum.subtract(expenseSum);

        return new ReportData(
                totalIncome,
                totalExpenses,
                netSavings
        );
    }

    private User getUser(String username) {

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));
    }

    private static class ReportData {

        private final Map<String, BigDecimal> totalIncome;
        private final Map<String, BigDecimal> totalExpenses;
        private final BigDecimal netSavings;

        private ReportData(
                Map<String, BigDecimal> totalIncome,
                Map<String, BigDecimal> totalExpenses,
                BigDecimal netSavings) {

            this.totalIncome = totalIncome;
            this.totalExpenses = totalExpenses;
            this.netSavings = netSavings;
        }
    }
}
