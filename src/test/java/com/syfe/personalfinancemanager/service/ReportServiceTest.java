package com.syfe.personalfinancemanager.service;

import com.syfe.personalfinancemanager.dto.report.MonthlyReportResponse;
import com.syfe.personalfinancemanager.dto.report.YearlyReportResponse;
import com.syfe.personalfinancemanager.entity.Category;
import com.syfe.personalfinancemanager.entity.Transaction;
import com.syfe.personalfinancemanager.entity.User;
import com.syfe.personalfinancemanager.exception.BadRequestException;
import com.syfe.personalfinancemanager.exception.ResourceNotFoundException;
import com.syfe.personalfinancemanager.repository.TransactionRepository;
import com.syfe.personalfinancemanager.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReportService reportService;

    @Test
    void monthlyReportSuccess() {

        User user = mock(User.class);

        Category salary =
                new Category(
                        "Salary",
                        "INCOME",
                        true,
                        null
                );

        Category freelance =
                new Category(
                        "Freelance",
                        "INCOME",
                        true,
                        null
                );

        Category food =
                new Category(
                        "Food",
                        "EXPENSE",
                        true,
                        null
                );

        Category rent =
                new Category(
                        "Rent",
                        "EXPENSE",
                        true,
                        null
                );

        Transaction salaryTransaction =
                new Transaction(
                        new BigDecimal("3000"),
                        LocalDate.of(2026, 10, 1),
                        salary,
                        "Salary",
                        user
                );

        Transaction freelanceTransaction =
                new Transaction(
                        new BigDecimal("500"),
                        LocalDate.of(2026, 10, 5),
                        freelance,
                        "Freelance work",
                        user
                );

        Transaction foodTransaction =
                new Transaction(
                        new BigDecimal("400"),
                        LocalDate.of(2026, 10, 10),
                        food,
                        "Food",
                        user
                );

        Transaction rentTransaction =
                new Transaction(
                        new BigDecimal("1200"),
                        LocalDate.of(2026, 10, 2),
                        rent,
                        "Rent",
                        user
                );

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(user));

        when(transactionRepository.findTransactions(
                user,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 31),
                null,
                null
        )).thenReturn(
                List.of(
                        salaryTransaction,
                        freelanceTransaction,
                        foodTransaction,
                        rentTransaction
                )
        );

        MonthlyReportResponse response =
                reportService.getMonthlyReport(
                        2026,
                        10,
                        "user@example.com"
                );

        assertEquals(10, response.getMonth());
        assertEquals(2026, response.getYear());

        assertEquals(
                new BigDecimal("3000"),
                response.getTotalIncome().get("Salary")
        );

        assertEquals(
                new BigDecimal("500"),
                response.getTotalIncome().get("Freelance")
        );

        assertEquals(
                new BigDecimal("400"),
                response.getTotalExpenses().get("Food")
        );

        assertEquals(
                new BigDecimal("1200"),
                response.getTotalExpenses().get("Rent")
        );

        assertEquals(
                new BigDecimal("1900"),
                response.getNetSavings()
        );
    }

    @Test
    void monthlyReportGroupsSameCategory() {

        User user = mock(User.class);

        Category food =
                new Category(
                        "Food",
                        "EXPENSE",
                        true,
                        null
                );

        Transaction first =
                new Transaction(
                        new BigDecimal("300"),
                        LocalDate.of(2026, 10, 1),
                        food,
                        "Lunch",
                        user
                );

        Transaction second =
                new Transaction(
                        new BigDecimal("200"),
                        LocalDate.of(2026, 10, 2),
                        food,
                        "Dinner",
                        user
                );

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(user));

        when(transactionRepository.findTransactions(
                user,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 31),
                null,
                null
        )).thenReturn(List.of(first, second));

        MonthlyReportResponse response =
                reportService.getMonthlyReport(
                        2026,
                        10,
                        "user@example.com"
                );

        assertEquals(
                new BigDecimal("500"),
                response.getTotalExpenses().get("Food")
        );

        assertEquals(
                new BigDecimal("-500"),
                response.getNetSavings()
        );
    }

    @Test
    void yearlyReportSuccess() {

        User user = mock(User.class);

        Category salary =
                new Category(
                        "Salary",
                        "INCOME",
                        true,
                        null
                );

        Category food =
                new Category(
                        "Food",
                        "EXPENSE",
                        true,
                        null
                );

        Transaction income =
                new Transaction(
                        new BigDecimal("36000"),
                        LocalDate.of(2026, 1, 1),
                        salary,
                        "Annual income",
                        user
                );

        Transaction expense =
                new Transaction(
                        new BigDecimal("12000"),
                        LocalDate.of(2026, 5, 1),
                        food,
                        "Expenses",
                        user
                );

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(user));

        when(transactionRepository.findTransactions(
                user,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                null,
                null
        )).thenReturn(List.of(income, expense));

        YearlyReportResponse response =
                reportService.getYearlyReport(
                        2026,
                        "user@example.com"
                );

        assertEquals(2026, response.getYear());

        assertEquals(
                new BigDecimal("36000"),
                response.getTotalIncome().get("Salary")
        );

        assertEquals(
                new BigDecimal("12000"),
                response.getTotalExpenses().get("Food")
        );

        assertEquals(
                new BigDecimal("24000"),
                response.getNetSavings()
        );
    }

    @Test
    void monthlyReportInvalidMonth() {

        assertThrows(
                BadRequestException.class,
                () -> reportService.getMonthlyReport(
                        2026,
                        13,
                        "user@example.com"
                )
        );
    }

    @Test
    void monthlyReportInvalidYear() {

        assertThrows(
                BadRequestException.class,
                () -> reportService.getMonthlyReport(
                        0,
                        10,
                        "user@example.com"
                )
        );
    }

    @Test
    void yearlyReportInvalidYear() {

        assertThrows(
                BadRequestException.class,
                () -> reportService.getYearlyReport(
                        0,
                        "user@example.com"
                )
        );
    }

    @Test
    void reportUserNotFound() {

        when(userRepository.findByUsername("missing@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> reportService.getYearlyReport(
                        2026,
                        "missing@example.com"
                )
        );
    }
}