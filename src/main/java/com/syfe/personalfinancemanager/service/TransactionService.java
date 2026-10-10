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

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Provides business logic for transaction creation,
 * retrieval, filtering, updating, and deletion.
 */
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository) {

        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Creates a transaction for the specified user.
     *
     * @param request transaction information
     * @param username transaction owner's username
     * @return created transaction
     * @throws BadRequestException if transaction data is invalid
     */
    public TransactionResponse createTransaction(
            CreateTransactionRequest request,
            String username) {

        User user = getUser(username);

        if (request.getAmount() == null
                || request.getAmount()
                .compareTo(BigDecimal.ZERO) <= 0) {

            throw new BadRequestException(
                    "Amount must be positive"
            );
        }

        if (request.getDate() == null
                || request.getDate()
                .isAfter(LocalDate.now())) {

            throw new BadRequestException(
                    "Invalid transaction date"
            );
        }

        if (request.getCategory() == null
                || request.getCategory().isBlank()) {

            throw new BadRequestException(
                    "Category is required"
            );
        }

        Category category =
                getAccessibleCategory(
                        request.getCategory(),
                        user
                );

        Transaction transaction =
                new Transaction(
                        request.getAmount(),
                        request.getDate(),
                        category,
                        request.getDescription(),
                        user
                );

        return toResponse(
                transactionRepository.save(transaction)
        );
    }

    /**
     * Returns transactions filtered by date,
     * category ID, and transaction type.
     *
     * @param username transaction owner's username
     * @param startDate optional start date
     * @param endDate optional end date
     * @param categoryId optional category identifier
     * @param type optional transaction type
     * @return matching transactions
     */
    public List<TransactionResponse> getTransactions(
            String username,
            LocalDate startDate,
            LocalDate endDate,
            Long categoryId,
            String type) {

        return getTransactions(
                username,
                startDate,
                endDate,
                categoryId,
                null,
                type
        );
    }

    /**
     * Returns transactions using optional date,
     * category, and transaction type filters.
     *
     * @param username transaction owner's username
     * @param startDate optional start date
     * @param endDate optional end date
     * @param categoryId optional category identifier
     * @param category optional category name
     * @param type optional transaction type
     * @return matching transactions
     * @throws BadRequestException if a filter is invalid
     */
    public List<TransactionResponse> getTransactions(
            String username,
            LocalDate startDate,
            LocalDate endDate,
            Long categoryId,
            String category,
            String type) {

        User user = getUser(username);

        if (startDate != null
                && endDate != null
                && startDate.isAfter(endDate)) {

            throw new BadRequestException(
                    "Invalid date range"
            );
        }

        if (type != null
                && !type.equals("INCOME")
                && !type.equals("EXPENSE")) {

            throw new BadRequestException(
                    "Invalid transaction type"
            );
        }

        Long finalCategoryId = categoryId;

        if (finalCategoryId == null
                && category != null
                && !category.isBlank()) {

            Category accessibleCategory =
                    getAccessibleCategory(
                            category,
                            user
                    );

            finalCategoryId =
                    accessibleCategory.getId();
        }

        return transactionRepository
                .findTransactions(
                        user,
                        startDate,
                        endDate,
                        finalCategoryId,
                        type
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Updates editable fields of a transaction.
     *
     * @param id transaction identifier
     * @param request updated transaction information
     * @param username transaction owner's username
     * @return updated transaction
     * @throws ResourceNotFoundException if the transaction does not exist
     * @throws ForbiddenException if the transaction belongs to another user
     * @throws BadRequestException if updated data is invalid
     */
    public TransactionResponse updateTransaction(
            Long id,
            UpdateTransactionRequest request,
            String username) {

        User user = getUser(username);

        Transaction transaction =
                getOwnedTransaction(id, user);

        if (request.getAmount() != null) {

            if (request.getAmount()
                    .compareTo(BigDecimal.ZERO) <= 0) {

                throw new BadRequestException(
                        "Amount must be positive"
                );
            }

            transaction.setAmount(
                    request.getAmount()
            );
        }

        if (request.getCategory() != null) {

            Category category =
                    getAccessibleCategory(
                            request.getCategory(),
                            user
                    );

            transaction.setCategory(category);
        }

        if (request.getDescription() != null) {

            transaction.setDescription(
                    request.getDescription()
            );
        }

        return toResponse(
                transactionRepository.save(transaction)
        );
    }

    /**
     * Deletes a transaction owned by the specified user.
     *
     * @param id transaction identifier
     * @param username transaction owner's username
     * @throws ResourceNotFoundException if the transaction does not exist
     * @throws ForbiddenException if the transaction belongs to another user
     */
    public void deleteTransaction(
            Long id,
            String username) {

        User user = getUser(username);

        Transaction transaction =
                getOwnedTransaction(
                        id,
                        user
                );

        transactionRepository.delete(transaction);
    }

    private Transaction getOwnedTransaction(
            Long id,
            User user) {

        Transaction transaction =
                transactionRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction not found"
                                )
                        );

        if (!transaction
                .getUser()
                .getId()
                .equals(user.getId())) {

            throw new ForbiddenException(
                    "You cannot access this transaction"
            );
        }

        return transaction;
    }

    private User getUser(
            String username) {

        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );
    }

    private Category getAccessibleCategory(
            String categoryName,
            User user) {

        return categoryRepository
                .findByNameAndUser(
                        categoryName,
                        user
                )
                .orElseGet(() ->
                        categoryRepository
                                .findByNameAndIsDefaultTrue(
                                        categoryName
                                )
                                .orElseThrow(() ->
                                        new BadRequestException(
                                                "Invalid category"
                                        )
                                )
                );
    }

    private TransactionResponse toResponse(
            Transaction transaction) {

        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getDate(),
                transaction.getCategory().getName(),
                transaction.getDescription(),
                transaction.getCategory().getType()
        );
    }
}