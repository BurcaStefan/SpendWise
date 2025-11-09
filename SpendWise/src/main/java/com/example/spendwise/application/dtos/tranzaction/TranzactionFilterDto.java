package com.example.spendwise.application.dtos.tranzaction;

import com.example.spendwise.domain.entities.CategoryType;
import com.example.spendwise.domain.entities.TranzactionType;

public class TranzactionFilterDto {
    private TranzactionType type;
    private CategoryType category;
    private String sortBy;
    private String sortDirection;

    public TranzactionType getType() { return type; }
    public void setType(TranzactionType type) { this.type = type; }

    public CategoryType getCategory() { return category; }
    public void setCategory(CategoryType category) { this.category = category; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getSortDirection() { return sortDirection; }
    public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }
}
