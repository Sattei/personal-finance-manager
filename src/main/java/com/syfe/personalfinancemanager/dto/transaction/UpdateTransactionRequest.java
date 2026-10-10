package com.syfe.personalfinancemanager.dto.transaction;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public class UpdateTransactionRequest {

    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = "Amount must be positive"
    )
    private BigDecimal amount;

    private String category;
    private String description;

    public UpdateTransactionRequest() {
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}