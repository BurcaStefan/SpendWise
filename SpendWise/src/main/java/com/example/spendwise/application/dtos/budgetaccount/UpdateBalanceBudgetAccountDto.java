package com.example.spendwise.application.dtos.budgetaccount;

import jakarta.validation.constraints.NotNull;

public class UpdateBalanceBudgetAccountDto {
    @NotNull
    public double value;

    public UpdateBalanceBudgetAccountDto(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
    public void setValue(double value) {
        this.value = value;
    }
}
