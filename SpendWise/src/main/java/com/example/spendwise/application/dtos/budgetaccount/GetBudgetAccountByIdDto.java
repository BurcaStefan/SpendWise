package com.example.spendwise.application.dtos.budgetaccount;

import java.util.UUID;

public class GetBudgetAccountByIdDto {

    private UUID budgetAccountId;
    private UUID userId;
    private double value;

    public GetBudgetAccountByIdDto() {
    }

    public GetBudgetAccountByIdDto(UUID budgetAccountId, UUID userId, double value) {
        this.budgetAccountId = budgetAccountId;
        this.userId = userId;
        this.value = value;
    }

    public UUID getBudgetAccountId() {
        return budgetAccountId;
    }

    public void setBudgetAccountId(UUID budgetAccountId) {
        this.budgetAccountId = budgetAccountId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
