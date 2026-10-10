package com.syfe.personalfinancemanager.dto.transaction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class UpdateTransactionRequest {

    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = "Amount must be positive"
    )
    @Digits(
            integer = 17,
            fraction = 2,
            message = "Amount can have at most 2 decimal places"
    )
    private BigDecimal amount;

    @Size(
            max = 255,
            message = "Category name must not exceed 255 characters"
    )
    private String category;

    @Size(
            max = 255,
            message = "Description must not exceed 255 characters"
    )
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