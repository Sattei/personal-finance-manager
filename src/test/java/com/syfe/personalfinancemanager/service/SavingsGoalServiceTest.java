package com.syfe.personalfinancemanager.service;

import com.syfe.personalfinancemanager.dto.goal.CreateGoalRequest;
import com.syfe.personalfinancemanager.dto.goal.GoalResponse;
import com.syfe.personalfinancemanager.dto.goal.UpdateGoalRequest;
import com.syfe.personalfinancemanager.entity.Category;
import com.syfe.personalfinancemanager.entity.SavingsGoal;
import com.syfe.personalfinancemanager.entity.Transaction;
import com.syfe.personalfinancemanager.entity.User;
import com.syfe.personalfinancemanager.exception.BadRequestException;
import com.syfe.personalfinancemanager.exception.ForbiddenException;
import com.syfe.personalfinancemanager.exception.ResourceNotFoundException;
import com.syfe.personalfinancemanager.repository.SavingsGoalRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SavingsGoalServiceTest {

    @Mock
    private SavingsGoalRepository savingsGoalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private SavingsGoalService savingsGoalService;

    @Test
    void createGoalSuccess() {

        User user = mock(User.class);

        CreateGoalRequest request = new CreateGoalRequest();
        request.setGoalName("Emergency Fund");
        request.setTargetAmount(new BigDecimal("10000"));
        request.setTargetDate(LocalDate.now().plusMonths(6));
        request.setStartDate(LocalDate.now());

        SavingsGoal goal = new SavingsGoal(
                "Emergency Fund",
                new BigDecimal("10000"),
                request.getTargetDate(),
                request.getStartDate(),
                user
        );

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(user));

        when(savingsGoalRepository.save(any(SavingsGoal.class)))
                .thenReturn(goal);

        when(transactionRepository.findTransactions(
                user,
                request.getStartDate(),
                null,
                null,
                null
        )).thenReturn(List.of());

        GoalResponse response =
                savingsGoalService.createGoal(
                        request,
                        "user@example.com"
                );

        assertEquals("Emergency Fund", response.getGoalName());
        assertEquals(new BigDecimal("10000"), response.getTargetAmount());
        assertEquals(BigDecimal.ZERO, response.getCurrentProgress());
    }

    @Test
    void createGoalInvalidAmount() {

        User user = mock(User.class);

        CreateGoalRequest request = new CreateGoalRequest();
        request.setGoalName("Emergency Fund");
        request.setTargetAmount(new BigDecimal("-100"));
        request.setTargetDate(LocalDate.now().plusMonths(6));

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(user));

        assertThrows(
                BadRequestException.class,
                () -> savingsGoalService.createGoal(
                        request,
                        "user@example.com"
                )
        );

        verify(savingsGoalRepository, never())
                .save(any(SavingsGoal.class));
    }

    @Test
    void createGoalPastTargetDate() {

        User user = mock(User.class);

        CreateGoalRequest request = new CreateGoalRequest();
        request.setGoalName("Emergency Fund");
        request.setTargetAmount(new BigDecimal("10000"));
        request.setTargetDate(LocalDate.now().minusDays(1));

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(user));

        assertThrows(
                BadRequestException.class,
                () -> savingsGoalService.createGoal(
                        request,
                        "user@example.com"
                )
        );
    }

    @Test
    void getGoalsCalculatesProgress() {

        User user = mock(User.class);

        LocalDate startDate =
                LocalDate.now().minusMonths(1);

        SavingsGoal goal = new SavingsGoal(
                "Laptop",
                new BigDecimal("10000"),
                LocalDate.now().plusMonths(6),
                startDate,
                user
        );

        Category incomeCategory =
                new Category(
                        "Salary",
                        "INCOME",
                        true,
                        null
                );

        Category expenseCategory =
                new Category(
                        "Food",
                        "EXPENSE",
                        true,
                        null
                );

        Transaction income =
                new Transaction(
                        new BigDecimal("5000"),
                        LocalDate.now(),
                        incomeCategory,
                        "Salary",
                        user
                );

        Transaction expense =
                new Transaction(
                        new BigDecimal("1000"),
                        LocalDate.now(),
                        expenseCategory,
                        "Food",
                        user
                );

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(user));

        when(savingsGoalRepository.findByUser(user))
                .thenReturn(List.of(goal));

        when(transactionRepository.findTransactions(
                user,
                startDate,
                null,
                null,
                null
        )).thenReturn(List.of(income, expense));

        List<GoalResponse> result =
                savingsGoalService.getGoals(
                        "user@example.com"
                );

        assertEquals(1, result.size());

        GoalResponse response = result.get(0);

        assertEquals(
                new BigDecimal("4000"),
                response.getCurrentProgress()
        );

        assertEquals(
                new BigDecimal("40.0"),
                response.getProgressPercentage()
        );

        assertEquals(
                new BigDecimal("6000"),
                response.getRemainingAmount()
        );
    }

    @Test
    void getGoalSuccess() {

        User user = mock(User.class);

        when(user.getId()).thenReturn(1L);

        SavingsGoal goal = new SavingsGoal(
                "Laptop",
                new BigDecimal("10000"),
                LocalDate.now().plusMonths(6),
                LocalDate.now(),
                user
        );

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(user));

        when(savingsGoalRepository.findById(1L))
                .thenReturn(Optional.of(goal));

        when(transactionRepository.findTransactions(
                user,
                goal.getStartDate(),
                null,
                null,
                null
        )).thenReturn(List.of());

        GoalResponse response =
                savingsGoalService.getGoal(
                        1L,
                        "user@example.com"
                );

        assertEquals("Laptop", response.getGoalName());
    }

    @Test
    void getGoalNotFound() {

        User user = mock(User.class);

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(user));

        when(savingsGoalRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> savingsGoalService.getGoal(
                        99L,
                        "user@example.com"
                )
        );
    }

    @Test
    void updateGoalSuccess() {

        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);

        SavingsGoal goal = new SavingsGoal(
                "Laptop",
                new BigDecimal("10000"),
                LocalDate.now().plusMonths(6),
                LocalDate.now(),
                user
        );

        UpdateGoalRequest request =
                new UpdateGoalRequest();

        request.setTargetAmount(
                new BigDecimal("12000")
        );

        request.setTargetDate(
                LocalDate.now().plusMonths(8)
        );

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(user));

        when(savingsGoalRepository.findById(1L))
                .thenReturn(Optional.of(goal));

        when(savingsGoalRepository.save(goal))
                .thenReturn(goal);

        when(transactionRepository.findTransactions(
                user,
                goal.getStartDate(),
                null,
                null,
                null
        )).thenReturn(List.of());

        GoalResponse response =
                savingsGoalService.updateGoal(
                        1L,
                        request,
                        "user@example.com"
                );

        assertEquals(
                new BigDecimal("12000"),
                response.getTargetAmount()
        );

        assertEquals(
                request.getTargetDate(),
                response.getTargetDate()
        );
    }

    @Test
    void updateGoalInvalidAmount() {

        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);

        SavingsGoal goal = new SavingsGoal(
                "Laptop",
                new BigDecimal("10000"),
                LocalDate.now().plusMonths(6),
                LocalDate.now(),
                user
        );

        UpdateGoalRequest request =
                new UpdateGoalRequest();

        request.setTargetAmount(
                new BigDecimal("-1")
        );

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(user));

        when(savingsGoalRepository.findById(1L))
                .thenReturn(Optional.of(goal));

        assertThrows(
                BadRequestException.class,
                () -> savingsGoalService.updateGoal(
                        1L,
                        request,
                        "user@example.com"
                )
        );
    }

    @Test
    void deleteGoalSuccess() {

        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);

        SavingsGoal goal = new SavingsGoal(
                "Laptop",
                new BigDecimal("10000"),
                LocalDate.now().plusMonths(6),
                LocalDate.now(),
                user
        );

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(user));

        when(savingsGoalRepository.findById(1L))
                .thenReturn(Optional.of(goal));

        savingsGoalService.deleteGoal(
                1L,
                "user@example.com"
        );

        verify(savingsGoalRepository)
                .delete(goal);
    }

    @Test
    void accessAnotherUsersGoalForbidden() {

        User currentUser = mock(User.class);
        User owner = mock(User.class);

        when(currentUser.getId()).thenReturn(1L);
        when(owner.getId()).thenReturn(2L);

        SavingsGoal goal = new SavingsGoal(
                "Laptop",
                new BigDecimal("10000"),
                LocalDate.now().plusMonths(6),
                LocalDate.now(),
                owner
        );

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(currentUser));

        when(savingsGoalRepository.findById(1L))
                .thenReturn(Optional.of(goal));

        assertThrows(
                ForbiddenException.class,
                () -> savingsGoalService.deleteGoal(
                        1L,
                        "user@example.com"
                )
        );

        verify(savingsGoalRepository, never())
                .delete(any(SavingsGoal.class));
    }
}