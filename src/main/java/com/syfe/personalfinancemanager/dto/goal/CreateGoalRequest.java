package com.syfe.personalfinancemanager.dto.goal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateGoalRequest {

    @NotBlank(message = "Goal name is required")
    private String goalName;

    @NotNull(message = "Target amount is required")
    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = "Target amount must be positive"
    )
    private BigDecimal targetAmount;

    @NotNull(message = "Target date is required")
    @Future(message = "Target date must be in the future")
    private LocalDate targetDate;

    private LocalDate startDate;

    public CreateGoalRequest() {
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

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
}