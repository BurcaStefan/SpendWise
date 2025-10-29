package com.example.spendwise.infrastructure.repositories.budgetaccount;

import com.example.spendwise.domain.entities.BudgetAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataBudgetAccountRepository extends JpaRepository<BudgetAccount, UUID> {
}
