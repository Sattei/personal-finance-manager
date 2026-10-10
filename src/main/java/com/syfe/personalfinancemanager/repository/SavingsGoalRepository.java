package com.syfe.personalfinancemanager.repository;

import com.syfe.personalfinancemanager.entity.SavingsGoal;
import com.syfe.personalfinancemanager.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavingsGoalRepository
        extends JpaRepository<SavingsGoal, Long> {

    List<SavingsGoal> findByUser(User user);

    Optional<SavingsGoal> findByIdAndUser(Long id, User user);
}