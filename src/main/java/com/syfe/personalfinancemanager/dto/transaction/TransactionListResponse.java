package com.syfe.personalfinancemanager.dto.transaction;

import java.util.List;

public class TransactionListResponse {

    private List<TransactionResponse> transactions;

    public TransactionListResponse(List<TransactionResponse> transactions) {
        this.transactions = transactions;
    }

    public List<TransactionResponse> getTransactions() {
        return transactions;
    }
}