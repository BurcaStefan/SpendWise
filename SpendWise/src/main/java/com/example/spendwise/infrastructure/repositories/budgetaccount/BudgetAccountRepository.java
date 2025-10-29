package com.example.spendwise.infrastructure.repositories.budgetaccount;

import com.example.spendwise.domain.entities.BudgetAccount;
import com.example.spendwise.domain.repositories.IBudgetAccountRepository;
import org.springframework.stereotype.Repository;

@Repository
public class BudgetAccountRepository implements IBudgetAccountRepository {
    private final SpringDataBudgetAccountRepository budgetAccountRepository;
    public BudgetAccountRepository(SpringDataBudgetAccountRepository springDataBudgetAccountRepository) {
        this.budgetAccountRepository = springDataBudgetAccountRepository;
    }

    @Override
    public BudgetAccount createBudgetAccount(BudgetAccount budgetAccount) {
        return budgetAccountRepository.save(budgetAccount);
    }
}
