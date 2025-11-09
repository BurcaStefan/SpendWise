package com.example.spendwise.application.strategy;

import com.example.spendwise.domain.entities.Tranzaction;
import com.example.spendwise.domain.entities.TranzactionType;
import org.springframework.data.jpa.domain.Specification;

public class TypeFilterStrategy implements TranzactionFilterStrategy {
    private final TranzactionType type;

    public TypeFilterStrategy(TranzactionType type) {
        this.type = type;
    }

    @Override
    public Specification<Tranzaction> buildSpecification() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("type"), type);
    }
}
