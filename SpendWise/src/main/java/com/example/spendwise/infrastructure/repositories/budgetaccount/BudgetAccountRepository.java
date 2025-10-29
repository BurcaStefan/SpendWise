package com.example.spendwise.infrastructure.repositories.budgetaccount;

import com.example.spendwise.application.dtos.budgetaccount.GetBudgetAccountByIdDto;
import com.example.spendwise.domain.entities.BudgetAccount;
import com.example.spendwise.domain.repositories.IBudgetAccountRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

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

    @Override
    public GetBudgetAccountByIdDto getBudgetAccountById(UUID id) {
        BudgetAccount budgetAccount = budgetAccountRepository.findById(id).orElse(null);
        if (budgetAccount == null) {
            return null;
        }

        GetBudgetAccountByIdDto dto = new GetBudgetAccountByIdDto();
        dto.setBudgetAccountId(budgetAccount.getBudgetAccountId());
        dto.setUserId(budgetAccount.getUserId());
        dto.setValue(budgetAccount.getValue());
        return dto;
    }
}
