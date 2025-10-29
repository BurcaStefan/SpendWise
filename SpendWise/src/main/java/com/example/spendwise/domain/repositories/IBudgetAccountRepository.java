package com.example.spendwise.domain.repositories;

import com.example.spendwise.application.dtos.budgetaccount.GetBudgetAccountByIdDto;
import com.example.spendwise.application.dtos.budgetaccount.UpdateBalanceBudgetAccountDto;
import com.example.spendwise.domain.entities.BudgetAccount;
import java.util.UUID;

public interface IBudgetAccountRepository {
    BudgetAccount createBudgetAccount(BudgetAccount budgetAccount);
    GetBudgetAccountByIdDto getBudgetAccountById(UUID id);
    BudgetAccount updateBudgetAccount(UUID id, UpdateBalanceBudgetAccountDto dto);
    boolean deleteBudgetAccount(UUID id);
}
