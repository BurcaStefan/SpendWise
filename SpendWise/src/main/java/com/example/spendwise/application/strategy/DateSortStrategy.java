package com.example.spendwise.application.strategy;

import org.springframework.data.domain.Sort;

public class DateSortStrategy implements TranzactionSortStrategy {
    private final boolean ascending;

    public DateSortStrategy(boolean ascending) {
        this.ascending = ascending;
    }

    @Override
    public Sort buildSort() {
        return ascending ? Sort.by("date").ascending() : Sort.by("date").descending();
    }
}
