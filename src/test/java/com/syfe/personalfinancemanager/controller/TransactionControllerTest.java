package com.syfe.personalfinancemanager.controller;

import com.syfe.personalfinancemanager.dto.common.MessageResponse;
import com.syfe.personalfinancemanager.dto.transaction.CreateTransactionRequest;
import com.syfe.personalfinancemanager.dto.transaction.TransactionListResponse;
import com.syfe.personalfinancemanager.dto.transaction.TransactionResponse;
import com.syfe.personalfinancemanager.dto.transaction.UpdateTransactionRequest;
import com.syfe.personalfinancemanager.service.TransactionService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TransactionController transactionController;

    @Test
    void createTransactionSuccess() {

        CreateTransactionRequest request =
                new CreateTransactionRequest();

        TransactionResponse serviceResponse =
                new TransactionResponse(
                        1L,
                        new BigDecimal("5000"),
                        LocalDate.of(2026, 10, 1),
                        "Salary",
                        "Salary payment",
                        "INCOME"
                );

        when(authentication.getName())
                .thenReturn("user@example.com");

        when(transactionService.createTransaction(
                request,
                "user@example.com"
        )).thenReturn(serviceResponse);

        ResponseEntity<TransactionResponse> response =
                transactionController.createTransaction(
                        request,
                        authentication
                );

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());

        assertEquals(
                "Salary",
                response.getBody().getCategory()
        );
    }

    @Test
    void getTransactionsSuccess() {

        when(authentication.getName())
                .thenReturn("user@example.com");

        TransactionResponse transaction =
                new TransactionResponse(
                        1L,
                        new BigDecimal("500"),
                        LocalDate.of(2026, 10, 1),
                        "Food",
                        "Dinner",
                        "EXPENSE"
                );

        when(transactionService.getTransactions(
                "user@example.com",
                null,
                null,
                null,
                null,
                null
        )).thenReturn(List.of(transaction));

        ResponseEntity<TransactionListResponse> response =
            transactionController.getTransactions(
                    null,
                    null,
                    null,
                    null,
                    null,
                    authentication
            );

        assertEquals(200, response.getStatusCode().value());

        assertEquals(
                1,
                response.getBody()
                        .getTransactions()
                        .size()
        );
    }

    @Test
    void updateTransactionSuccess() {

        UpdateTransactionRequest request =
                new UpdateTransactionRequest();

        request.setAmount(
                new BigDecimal("700")
        );

        TransactionResponse serviceResponse =
                new TransactionResponse(
                        1L,
                        new BigDecimal("700"),
                        LocalDate.of(2026, 10, 1),
                        "Food",
                        "Updated",
                        "EXPENSE"
                );

        when(authentication.getName())
                .thenReturn("user@example.com");

        when(transactionService.updateTransaction(
                1L,
                request,
                "user@example.com"
        )).thenReturn(serviceResponse);

        ResponseEntity<TransactionResponse> response =
                transactionController.updateTransaction(
                        1L,
                        request,
                        authentication
                );

        assertEquals(200, response.getStatusCode().value());

        assertEquals(
                new BigDecimal("700"),
                response.getBody().getAmount()
        );
    }

    @Test
    void deleteTransactionSuccess() {

        when(authentication.getName())
                .thenReturn("user@example.com");

        ResponseEntity<MessageResponse> response =
                transactionController.deleteTransaction(
                        1L,
                        authentication
                );

        assertEquals(200, response.getStatusCode().value());

        assertEquals(
                "Transaction deleted successfully",
                response.getBody().getMessage()
        );

        verify(transactionService)
                .deleteTransaction(
                        1L,
                        "user@example.com"
                );
    }
}