package com.syfe.personalfinancemanager.service;

import com.syfe.personalfinancemanager.dto.transaction.CreateTransactionRequest;
import com.syfe.personalfinancemanager.dto.transaction.TransactionResponse;
import com.syfe.personalfinancemanager.dto.transaction.UpdateTransactionRequest;
import com.syfe.personalfinancemanager.entity.Category;
import com.syfe.personalfinancemanager.entity.Transaction;
import com.syfe.personalfinancemanager.entity.User;
import com.syfe.personalfinancemanager.exception.BadRequestException;
import com.syfe.personalfinancemanager.exception.ForbiddenException;
import com.syfe.personalfinancemanager.exception.ResourceNotFoundException;
import com.syfe.personalfinancemanager.repository.CategoryRepository;
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
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void createTransactionSuccess() {

        User user = mock(User.class);

        Category category = new Category(
                "Salary",
                "INCOME",
                true,
                null
        );

        CreateTransactionRequest request =
                new CreateTransactionRequest();

        request.setAmount(new BigDecimal("50000"));
        request.setDate(LocalDate.now());
        request.setCategory("Salary");
        request.setDescription("Monthly salary");

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(user));

        when(categoryRepository.findByNameAndUser(
                "Salary",
                user
        )).thenReturn(Optional.empty());

        when(categoryRepository.findByNameAndIsDefaultTrue(
                "Salary"
        )).thenReturn(Optional.of(category));

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse response =
                transactionService.createTransaction(
                        request,
                        "user@example.com"
                );

        assertEquals(
                new BigDecimal("50000"),
                response.getAmount()
        );

        assertEquals(
                "Salary",
                response.getCategory()
        );

        assertEquals(
                "INCOME",
                response.getType()
        );

        verify(transactionRepository)
                .save(any(Transaction.class));
    }

    @Test
    void createTransactionNegativeAmount() {

        User user = mock(User.class);

        CreateTransactionRequest request =
                new CreateTransactionRequest();

        request.setAmount(new BigDecimal("-100"));
        request.setDate(LocalDate.now());
        request.setCategory("Food");

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(user));

        assertThrows(
                BadRequestException.class,
                () -> transactionService.createTransaction(
                        request,
                        "user@example.com"
                )
        );

        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }

    @Test
    void createTransactionFutureDate() {

        User user = mock(User.class);

        CreateTransactionRequest request =
                new CreateTransactionRequest();

        request.setAmount(new BigDecimal("100"));
        request.setDate(LocalDate.now().plusDays(1));
        request.setCategory("Food");

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(user));

        assertThrows(
                BadRequestException.class,
                () -> transactionService.createTransaction(
                        request,
                        "user@example.com"
                )
        );
    }

    @Test
    void createTransactionInvalidCategory() {

        User user = mock(User.class);

        CreateTransactionRequest request =
                new CreateTransactionRequest();

        request.setAmount(new BigDecimal("100"));
        request.setDate(LocalDate.now());
        request.setCategory("Unknown");

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(user));

        when(categoryRepository.findByNameAndUser(
                "Unknown",
                user
        )).thenReturn(Optional.empty());

        when(categoryRepository.findByNameAndIsDefaultTrue(
                "Unknown"
        )).thenReturn(Optional.empty());

        assertThrows(
                BadRequestException.class,
                () -> transactionService.createTransaction(
                        request,
                        "user@example.com"
                )
        );
    }

    @Test
    void getTransactionsSuccess() {

        User user = mock(User.class);

        Category food = new Category(
                "Food",
                "EXPENSE",
                true,
                null
        );

        Transaction transaction =
                new Transaction(
                        new BigDecimal("500"),
                        LocalDate.now(),
                        food,
                        "Dinner",
                        user
                );

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(user));

        when(transactionRepository.findTransactions(
                user,
                null,
                null,
                null,
                null
        )).thenReturn(List.of(transaction));

        List<TransactionResponse> result =
                transactionService.getTransactions(
                        "user@example.com",
                        null,
                        null,
                        null,
                        null
                );

        assertEquals(1, result.size());
        assertEquals("Food", result.get(0).getCategory());
        assertEquals("EXPENSE", result.get(0).getType());
    }

    @Test
    void getTransactionsInvalidDateRange() {

        User user = mock(User.class);

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(user));

        LocalDate start =
                LocalDate.of(2026, 10, 10);

        LocalDate end =
                LocalDate.of(2026, 10, 1);

        assertThrows(
                BadRequestException.class,
                () -> transactionService.getTransactions(
                        "user@example.com",
                        start,
                        end,
                        null,
                        null
                )
        );
    }

    @Test
    void getTransactionsInvalidType() {

        User user = mock(User.class);

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(user));

        assertThrows(
                BadRequestException.class,
                () -> transactionService.getTransactions(
                        "user@example.com",
                        null,
                        null,
                        null,
                        "INVALID"
                )
        );
    }

    @Test
    void updateTransactionSuccess() {

        User user = mock(User.class);

        when(user.getId()).thenReturn(1L);

        Category oldCategory =
                new Category(
                        "Food",
                        "EXPENSE",
                        true,
                        null
                );

        Category newCategory =
                new Category(
                        "Salary",
                        "INCOME",
                        true,
                        null
                );

        Transaction transaction =
                new Transaction(
                        new BigDecimal("100"),
                        LocalDate.now(),
                        oldCategory,
                        "Old description",
                        user
                );

        UpdateTransactionRequest request =
                new UpdateTransactionRequest();

        request.setAmount(new BigDecimal("200"));
        request.setCategory("Salary");
        request.setDescription("Updated");

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(user));

        when(transactionRepository.findById(1L))
                .thenReturn(Optional.of(transaction));

        when(categoryRepository.findByNameAndUser(
                "Salary",
                user
        )).thenReturn(Optional.empty());

        when(categoryRepository.findByNameAndIsDefaultTrue(
                "Salary"
        )).thenReturn(Optional.of(newCategory));

        when(transactionRepository.save(transaction))
                .thenReturn(transaction);

        TransactionResponse response =
                transactionService.updateTransaction(
                        1L,
                        request,
                        "user@example.com"
                );

        assertEquals(
                new BigDecimal("200"),
                response.getAmount()
        );

        assertEquals("Salary", response.getCategory());
        assertEquals("Updated", response.getDescription());
        assertEquals("INCOME", response.getType());
    }

    @Test
    void updateTransactionNotFound() {

        User user = mock(User.class);

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(user));

        when(transactionRepository.findById(99L))
                .thenReturn(Optional.empty());

        UpdateTransactionRequest request =
                new UpdateTransactionRequest();

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionService.updateTransaction(
                        99L,
                        request,
                        "user@example.com"
                )
        );
    }

    @Test
    void deleteTransactionSuccess() {

        User user = mock(User.class);

        when(user.getId()).thenReturn(1L);

        Category category =
                new Category(
                        "Food",
                        "EXPENSE",
                        true,
                        null
                );

        Transaction transaction =
                new Transaction(
                        new BigDecimal("100"),
                        LocalDate.now(),
                        category,
                        "Lunch",
                        user
                );

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(user));

        when(transactionRepository.findById(1L))
                .thenReturn(Optional.of(transaction));

        transactionService.deleteTransaction(
                1L,
                "user@example.com"
        );

        verify(transactionRepository)
                .delete(transaction);
    }

    @Test
    void accessAnotherUsersTransactionForbidden() {

        User currentUser = mock(User.class);
        User owner = mock(User.class);

        when(currentUser.getId()).thenReturn(1L);
        when(owner.getId()).thenReturn(2L);

        Category category =
                new Category(
                        "Food",
                        "EXPENSE",
                        true,
                        null
                );

        Transaction transaction =
                new Transaction(
                        new BigDecimal("100"),
                        LocalDate.now(),
                        category,
                        "Lunch",
                        owner
                );

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(currentUser));

        when(transactionRepository.findById(1L))
                .thenReturn(Optional.of(transaction));

        assertThrows(
                ForbiddenException.class,
                () -> transactionService.deleteTransaction(
                        1L,
                        "user@example.com"
                )
        );

        verify(transactionRepository, never())
                .delete(any(Transaction.class));
    }
}