package com.syfe.personalfinancemanager.dto.goal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateGoalRequest {

    @NotBlank(message = "Goal name is required")
    @Size(
            max = 255,
            message = "Goal name must not exceed 255 characters"
    )
    private String goalName;

    @NotNull(message = "Target amount is required")
    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = "Target amount must be positive"
    )
    @Digits(
            integer = 17,
            fraction = 2,
            message = "Target amount can have at most 2 decimal places"
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