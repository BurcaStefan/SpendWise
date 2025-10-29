package com.example.spendwise.application.services;

import com.example.spendwise.application.dtos.budgetaccount.CreateBudgetAccountDto;
import com.example.spendwise.application.factory.EntityFactory;
import com.example.spendwise.domain.entities.BudgetAccount;
import com.example.spendwise.domain.repositories.IBudgetAccountRepository;
import com.example.spendwise.domain.repositories.IUserRepository;
import org.springframework.stereotype.Service;

@Service
public class BudgetAccountServices {
    private final IBudgetAccountRepository budgetAccountRepository;
    private final EntityFactory<BudgetAccount, CreateBudgetAccountDto> budgetAccountFactory;
    private final IUserRepository userRepository;

    public BudgetAccountServices(IBudgetAccountRepository budgetAccountRepository,
                                 EntityFactory<BudgetAccount, CreateBudgetAccountDto> budgetAccountFactory,
                                 IUserRepository userRepository) {
        this.userRepository = userRepository;
        this.budgetAccountRepository = budgetAccountRepository;
        this.budgetAccountFactory = budgetAccountFactory;
    }

    public BudgetAccount createBudgetAccount(CreateBudgetAccountDto dto) {
        if(userRepository.getUserById(dto.getUserId())==null){
            throw new IllegalArgumentException("User with the given ID does not exist.");
        }

        BudgetAccount budgetAccount = budgetAccountFactory.create(dto);
        return budgetAccountRepository.createBudgetAccount(budgetAccount);
    }
}
