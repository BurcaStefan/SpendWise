package com.example.spendwise.application.strategy;

import org.springframework.data.domain.Sort;

public class ValueSortStrategy implements TranzactionSortStrategy {
    private final boolean ascending;

    public ValueSortStrategy(boolean ascending) {
        this.ascending = ascending;
    }

    @Override
    public Sort buildSort() {
        return ascending ? Sort.by("value").ascending() : Sort.by("value").descending();
    }
}
