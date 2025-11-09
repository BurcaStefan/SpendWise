package com.example.spendwise.application.strategy;

import com.example.spendwise.domain.entities.Tranzaction;
import org.springframework.data.jpa.domain.Specification;

public interface TranzactionFilterStrategy {
    Specification<Tranzaction> buildSpecification();
}