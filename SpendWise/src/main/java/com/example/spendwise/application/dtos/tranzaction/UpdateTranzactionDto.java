package com.example.spendwise.application.dtos.tranzaction;

import com.example.spendwise.domain.entities.CategoryType;
import com.example.spendwise.domain.entities.TranzactionType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class UpdateTranzactionDto {

    @NotNull
    public TranzactionType tranzactionType;
    @NotNull
    public CategoryType categoryType;
    @NotNull
    public double value;
    @NotNull
    public LocalDate date;
    @NotNull
    public boolean recurrent;
    public String description;

    public UpdateTranzactionDto() {}

    public UpdateTranzactionDto(TranzactionType tranzactionType, CategoryType categoryType, LocalDate date, double value, boolean recurrent, String description) {
        this.tranzactionType = tranzactionType;
        this.categoryType = categoryType;
        this.date = date;
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

    public CategoryType getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(CategoryType categoryType) {
        this.categoryType = categoryType;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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
