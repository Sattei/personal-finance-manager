package com.syfe.personalfinancemanager.dto.goal;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GoalResponse {

    private Long id;
    private String goalName;
    private BigDecimal targetAmount;
    private LocalDate targetDate;
    private LocalDate startDate;
    private BigDecimal currentProgress;
    private BigDecimal progressPercentage;
    private BigDecimal remainingAmount;

    public GoalResponse(
            Long id,
            String goalName,
            BigDecimal targetAmount,
            LocalDate targetDate,
            LocalDate startDate,
            BigDecimal currentProgress,
            BigDecimal progressPercentage,
            BigDecimal remainingAmount) {

        this.id = id;
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.targetDate = targetDate;
        this.startDate = startDate;
        this.currentProgress = currentProgress;
        this.progressPercentage = progressPercentage;
        this.remainingAmount = remainingAmount;
    }

    public Long getId() {
        return id;
    }

    public String getGoalName() {
        return goalName;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public BigDecimal getCurrentProgress() {
        return currentProgress;
    }

    public BigDecimal getProgressPercentage() {
        return progressPercentage;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }
}