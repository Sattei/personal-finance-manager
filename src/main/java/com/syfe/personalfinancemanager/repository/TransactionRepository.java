package com.syfe.personalfinancemanager.repository;

import com.syfe.personalfinancemanager.entity.Category;
import com.syfe.personalfinancemanager.entity.Transaction;
import com.syfe.personalfinancemanager.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByIdAndUser(Long id, User user);

    boolean existsByCategory(Category category);

    @Query("""
            SELECT t
            FROM Transaction t
            WHERE t.user = :user
              AND (:startDate IS NULL OR t.date >= :startDate)
              AND (:endDate IS NULL OR t.date <= :endDate)
              AND (:categoryId IS NULL OR t.category.id = :categoryId)
              AND (:type IS NULL OR t.category.type = :type)
            ORDER BY t.date DESC, t.id DESC
            """)
    List<Transaction> findTransactions(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("categoryId") Long categoryId,
            @Param("type") String type
    );
}