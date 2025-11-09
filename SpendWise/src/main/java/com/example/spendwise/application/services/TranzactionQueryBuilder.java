package com.example.spendwise.application.services;

import com.example.spendwise.application.dtos.tranzaction.TranzactionFilterDto;
import com.example.spendwise.application.strategy.*;
import com.example.spendwise.domain.entities.Tranzaction;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TranzactionQueryBuilder {

    public Specification<Tranzaction> buildSpecification(TranzactionFilterDto filterDto) {
        List<TranzactionFilterStrategy> strategies = new ArrayList<>();

        if (filterDto.getType() != null) {
            strategies.add(new TypeFilterStrategy(filterDto.getType()));
        }
        if (filterDto.getCategory() != null) {
            strategies.add(new CategoryFilterStrategy(filterDto.getCategory()));
        }

        return strategies.stream()
                .map(TranzactionFilterStrategy::buildSpecification)
                .reduce(Specification::and)
                .orElse(null);
    }

    public Sort buildSort(TranzactionFilterDto filterDto) {
        TranzactionSortStrategy sortStrategy = createSortStrategy(filterDto);
        return sortStrategy != null ? sortStrategy.buildSort() : Sort.by("date").descending();
    }

    private TranzactionSortStrategy createSortStrategy(TranzactionFilterDto filterDto) {
        String sortBy = filterDto.getSortBy();
        boolean ascending = "asc".equalsIgnoreCase(filterDto.getSortDirection());

        if ("value".equalsIgnoreCase(sortBy)) {
            return new ValueSortStrategy(ascending);
        } else if ("date".equalsIgnoreCase(sortBy)) {
            return new DateSortStrategy(ascending);
        }
        return null;
    }
}
