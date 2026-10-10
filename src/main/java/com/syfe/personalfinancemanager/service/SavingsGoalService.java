package com.syfe.personalfinancemanager.service;

import com.syfe.personalfinancemanager.dto.goal.CreateGoalRequest;
import com.syfe.personalfinancemanager.dto.goal.GoalResponse;
import com.syfe.personalfinancemanager.dto.goal.UpdateGoalRequest;
import com.syfe.personalfinancemanager.entity.SavingsGoal;
import com.syfe.personalfinancemanager.entity.Transaction;
import com.syfe.personalfinancemanager.entity.User;
import com.syfe.personalfinancemanager.exception.BadRequestException;
import com.syfe.personalfinancemanager.exception.ForbiddenException;
import com.syfe.personalfinancemanager.exception.ResourceNotFoundException;
import com.syfe.personalfinancemanager.repository.SavingsGoalRepository;
import com.syfe.personalfinancemanager.repository.TransactionRepository;
import com.syfe.personalfinancemanager.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * Manages user savings goals and calculates progress from income and expenses.
 */
@Service
public class SavingsGoalService {

    private final SavingsGoalRepository savingsGoalRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public SavingsGoalService(
            SavingsGoalRepository savingsGoalRepository,
            UserRepository userRepository,
            TransactionRepository transactionRepository) {

        this.savingsGoalRepository = savingsGoalRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Validates and creates a savings goal for a user.
     *
     * @param request goal data
     * @param username username of the goal owner
     * @return the created goal with calculated progress
     * @throws BadRequestException if the goal name, amount, or dates are invalid
     */
    public GoalResponse createGoal(
            CreateGoalRequest request,
            String username) {

        User user = getUser(username);

        if (request.getGoalName() == null
                || request.getGoalName().isBlank()) {

            throw new BadRequestException(
                    "Goal name is required"
            );
        }

        if (request.getTargetAmount() == null
                || request.getTargetAmount()
                .compareTo(BigDecimal.ZERO) <= 0) {

            throw new BadRequestException(
                    "Target amount must be positive"
            );
        }

        if (request.getTargetDate() == null
                || !request.getTargetDate()
                .isAfter(LocalDate.now())) {

            throw new BadRequestException(
                    "Target date must be in the future"
            );
        }

        LocalDate startDate =
                request.getStartDate();

        if (startDate == null) {
            startDate = LocalDate.now();
        }

        if (startDate.isAfter(request.getTargetDate())) {

            throw new BadRequestException(
                    "Start date cannot be after target date"
            );
        }

        SavingsGoal goal =
                new SavingsGoal(
                        request.getGoalName(),
                        request.getTargetAmount(),
                        request.getTargetDate(),
                        startDate,
                        user
                );

        return toResponse(
                savingsGoalRepository.save(goal)
        );
    }

    /**
     * Returns all savings goals owned by a user.
     *
     * @param username username of the goal owner
     * @return the user's goals with calculated progress
     */
    public List<GoalResponse> getGoals(
            String username) {

        User user = getUser(username);

        return savingsGoalRepository
                .findByUser(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Returns a savings goal owned by a user.
     *
     * @param id goal identifier
     * @param username username of the goal owner
     * @return the requested goal with calculated progress
     * @throws ResourceNotFoundException if the goal does not exist
     * @throws ForbiddenException if the goal belongs to another user
     */
    public GoalResponse getGoal(
            Long id,
            String username) {

        User user = getUser(username);

        return toResponse(
                getOwnedGoal(id, user)
        );
    }

    /**
     * Updates the target amount or target date of a user's savings goal.
     *
     * @param id goal identifier
     * @param request goal updates
     * @param username username of the goal owner
     * @return the updated goal with calculated progress
     * @throws ResourceNotFoundException if the goal does not exist
     * @throws ForbiddenException if the goal belongs to another user
     * @throws BadRequestException if an updated target amount or date is invalid
     */
    public GoalResponse updateGoal(
            Long id,
            UpdateGoalRequest request,
            String username) {

        User user = getUser(username);

        SavingsGoal goal =
                getOwnedGoal(id, user);

        if (request.getTargetAmount() != null) {

            if (request.getTargetAmount()
                    .compareTo(BigDecimal.ZERO) <= 0) {

                throw new BadRequestException(
                        "Target amount must be positive"
                );
            }

            goal.setTargetAmount(
                    request.getTargetAmount()
            );
        }

        if (request.getTargetDate() != null) {

            if (!request.getTargetDate()
                    .isAfter(LocalDate.now())) {

                throw new BadRequestException(
                        "Target date must be in the future"
                );
            }

            if (goal.getStartDate()
                    .isAfter(request.getTargetDate())) {

                throw new BadRequestException(
                        "Start date cannot be after target date"
                );
            }

            goal.setTargetDate(
                    request.getTargetDate()
            );
        }

        return toResponse(
                savingsGoalRepository.save(goal)
        );
    }

    /**
     * Deletes a savings goal owned by a user.
     *
     * @param id goal identifier
     * @param username username of the goal owner
     * @throws ResourceNotFoundException if the goal does not exist
     * @throws ForbiddenException if the goal belongs to another user
     */
    public void deleteGoal(
            Long id,
            String username) {

        User user = getUser(username);

        SavingsGoal goal =
                getOwnedGoal(id, user);

        savingsGoalRepository.delete(goal);
    }

    private SavingsGoal getOwnedGoal(
            Long id,
            User user) {

        SavingsGoal goal =
                savingsGoalRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Goal not found"
                                )
                        );

        if (!goal.getUser()
                .getId()
                .equals(user.getId())) {

            throw new ForbiddenException(
                    "You cannot access this goal"
            );
        }

        return goal;
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

    private GoalResponse toResponse(
            SavingsGoal goal) {

        List<Transaction> transactions =
                transactionRepository.findTransactions(
                        goal.getUser(),
                        goal.getStartDate(),
                        null,
                        null,
                        null
                );

        BigDecimal income =
                BigDecimal.ZERO;

        BigDecimal expense =
                BigDecimal.ZERO;

        for (Transaction transaction : transactions) {

            if (transaction.getCategory()
                    .getType()
                    .equals("INCOME")) {

                income = income.add(
                        transaction.getAmount()
                );

            } else if (transaction.getCategory()
                    .getType()
                    .equals("EXPENSE")) {

                expense = expense.add(
                        transaction.getAmount()
                );
            }
        }

        BigDecimal currentProgress =
                income.subtract(expense);

        BigDecimal progressPercentage =
                currentProgress
                        .multiply(BigDecimal.valueOf(100))
                        .divide(
                                goal.getTargetAmount(),
                                2,
                                RoundingMode.HALF_UP
                        );

        progressPercentage =
                normalizePercentage(
                        progressPercentage
                );

        BigDecimal remainingAmount =
                goal.getTargetAmount()
                        .subtract(currentProgress);

        return new GoalResponse(
                goal.getId(),
                goal.getGoalName(),
                goal.getTargetAmount(),
                goal.getTargetDate(),
                goal.getStartDate(),
                currentProgress,
                progressPercentage,
                remainingAmount
        );
    }

    private BigDecimal normalizePercentage(
            BigDecimal value) {

        BigDecimal normalized =
                value.stripTrailingZeros();

        if (normalized.scale() < 1) {
            normalized =
                    normalized.setScale(1);
        }

        return normalized;
    }
}
