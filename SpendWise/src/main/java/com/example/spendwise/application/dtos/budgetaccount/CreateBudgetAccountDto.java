package com.example.spendwise.application.dtos.budgetaccount;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CreateBudgetAccountDto {
    @NotNull
    public UUID userId;

    public CreateBudgetAccountDto(UUID userId) {
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
