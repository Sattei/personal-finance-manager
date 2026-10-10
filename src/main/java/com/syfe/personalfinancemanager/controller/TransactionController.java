package com.syfe.personalfinancemanager.controller;

import com.syfe.personalfinancemanager.dto.common.MessageResponse;
import com.syfe.personalfinancemanager.dto.transaction.CreateTransactionRequest;
import com.syfe.personalfinancemanager.dto.transaction.TransactionListResponse;
import com.syfe.personalfinancemanager.dto.transaction.TransactionResponse;
import com.syfe.personalfinancemanager.dto.transaction.UpdateTransactionRequest;
import com.syfe.personalfinancemanager.service.TransactionService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Handles transaction creation, retrieval, updates, and deletion for authenticated users.
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Creates a transaction for the authenticated user.
     *
     * @param request validated transaction data
     * @param authentication current authenticated user
     * @return the created transaction
     */
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody CreateTransactionRequest request,
            Authentication authentication) {

        TransactionResponse response =
                transactionService.createTransaction(
                        request,
                        authentication.getName()
                );

        return ResponseEntity
                .status(201)
                .body(response);
    }

    /**
     * Lists the authenticated user's transactions using the supplied optional filters.
     *
     * @param startDate earliest transaction date to include
     * @param endDate latest transaction date to include
     * @param categoryId category identifier to filter by
     * @param category category name to filter by
     * @param type transaction type to filter by
     * @param authentication current authenticated user
     * @return matching transactions
     */
    @GetMapping
    public ResponseEntity<TransactionListResponse> getTransactions(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            Authentication authentication) {

        List<TransactionResponse> transactions =
                transactionService.getTransactions(
                        authentication.getName(),
                        startDate,
                        endDate,
                        categoryId,
                        category,
                        type
                );

        return ResponseEntity.ok(
                new TransactionListResponse(transactions)
        );
    }

    /**
     * Updates an existing transaction owned by the authenticated user.
     *
     * @param id transaction identifier
     * @param request validated transaction updates
     * @param authentication current authenticated user
     * @return the updated transaction
     */
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTransactionRequest request,
            Authentication authentication) {

        TransactionResponse response =
                transactionService.updateTransaction(
                        id,
                        request,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a transaction owned by the authenticated user.
     *
     * @param id transaction identifier
     * @param authentication current authenticated user
     * @return a successful deletion response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteTransaction(
            @PathVariable Long id,
            Authentication authentication) {

        transactionService.deleteTransaction(
                id,
                authentication.getName()
        );

        return ResponseEntity.ok(
                new MessageResponse(
                        "Transaction deleted successfully"
                )
        );
    }
}
