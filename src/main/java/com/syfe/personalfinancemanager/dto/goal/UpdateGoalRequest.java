package com.syfe.personalfinancemanager.dto.goal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UpdateGoalRequest {

    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = "Target amount must be positive"
    )
    private BigDecimal targetAmount;

    @Future(message = "Target date must be in the future")
    private LocalDate targetDate;

    public UpdateGoalRequest() {
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }
}