package com.example.spendwise.application.strategy;

import com.example.spendwise.domain.entities.CategoryType;
import com.example.spendwise.domain.entities.Tranzaction;
import org.springframework.data.jpa.domain.Specification;

public class CategoryFilterStrategy implements TranzactionFilterStrategy {
    private final CategoryType category;

    public CategoryFilterStrategy(CategoryType category) {
        this.category = category;
    }

    @Override
    public Specification<Tranzaction> buildSpecification() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("category"), category);
    }
}
