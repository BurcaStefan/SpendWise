package com.example.spendwise.domain.factory;

import com.example.spendwise.application.dtos.budgetaccount.CreateBudgetAccountDto;
import com.example.spendwise.domain.entities.BudgetAccount;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("budgetAccountFactory")
public class BudgetAccountFactory implements EntityFactory<BudgetAccount, CreateBudgetAccountDto> {
    @Override
    public BudgetAccount create(CreateBudgetAccountDto dto) {
        BudgetAccount budgetAccount = new BudgetAccount();
        budgetAccount.setBudgetAccountId(UUID.randomUUID());
        budgetAccount.setUserId(dto.getUserId());
        return budgetAccount;
    }

}
