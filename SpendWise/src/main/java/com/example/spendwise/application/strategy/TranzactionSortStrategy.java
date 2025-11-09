package com.example.spendwise.application.strategy;

import org.springframework.data.domain.Sort;

public interface TranzactionSortStrategy {
    Sort buildSort();
}