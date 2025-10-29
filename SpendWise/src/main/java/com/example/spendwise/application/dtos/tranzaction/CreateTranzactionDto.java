package com.example.spendwise.application.dtos.tranzaction;
import com.example.spendwise.domain.entities.TranzactionType;
import com.example.spendwise.domain.entities.CategoryType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public class CreateTranzactionDto {
    @NotNull
    public UUID accountId;
    @NotNull
    public TranzactionType tranzactionType;
    @NotNull
    public CategoryType categoryType;
    @NotNull
    @Positive
    public double value;
    @NotNull
    public boolean recurrent;
    public String description;

    public CreateTranzactionDto(UUID accountId, TranzactionType tranzactionType, CategoryType categoryType,
                               double value, boolean recurrent, String description) {
        this.accountId = accountId;
        this.tranzactionType = tranzactionType;
        this.categoryType = categoryType;
        this.value = value;
        this.recurrent = recurrent;
        this.description = description;
    }

    public TranzactionType getTranzactionType() {
        return tranzactionType;
    }

    public void setTranzactionType(TranzactionType tranzactionType) {
        this.tranzactionType = tranzactionType;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public CategoryType getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(CategoryType categoryType) {
        this.categoryType = categoryType;
    }

    public boolean isRecurrent() {
        return recurrent;
    }

    public void setRecurrent(boolean recurrent) {
        this.recurrent = recurrent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
