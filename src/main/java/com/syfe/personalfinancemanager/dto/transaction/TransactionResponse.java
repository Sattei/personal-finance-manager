package com.syfe.personalfinancemanager.dto.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionResponse {

    private Long id;
    private BigDecimal amount;
    private LocalDate date;
    private String category;
    private String description;
    private String type;

    public TransactionResponse(
            Long id,
            BigDecimal amount,
            LocalDate date,
            String category,
            String description,
            String type) {

        this.id = id;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.description = description;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }
}