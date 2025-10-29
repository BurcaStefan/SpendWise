package com.example.spendwise.domain.repositories;

import com.example.spendwise.application.dtos.budgetaccount.CreateBudgetAccountDto;
import com.example.spendwise.domain.entities.BudgetAccount;

public interface IBudgetAccountRepository {
    BudgetAccount createBudgetAccount(BudgetAccount budgetAccount);
}
