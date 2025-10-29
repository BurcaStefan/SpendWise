package com.example.spendwise.application.services;

import com.example.spendwise.application.dtos.budgetaccount.CreateBudgetAccountDto;
import com.example.spendwise.application.dtos.budgetaccount.GetBudgetAccountByIdDto;
import com.example.spendwise.application.dtos.budgetaccount.UpdateBalanceBudgetAccountDto;
import com.example.spendwise.application.factory.EntityFactory;
import com.example.spendwise.domain.entities.BudgetAccount;
import com.example.spendwise.domain.repositories.IBudgetAccountRepository;
import com.example.spendwise.domain.repositories.IUserRepository;
import com.example.spendwise.infrastructure.repositories.budgetaccount.BudgetAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.UUID;


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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        BudgetAccount budgetAccount = budgetAccountFactory.create(dto);
        return budgetAccountRepository.createBudgetAccount(budgetAccount);
    }

    public GetBudgetAccountByIdDto getBudgetAccountById(UUID budgetAccountId) {
        if(budgetAccountRepository.getBudgetAccountById(budgetAccountId)==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Budget Account not found");
        }

        return budgetAccountRepository.getBudgetAccountById(budgetAccountId);
    }

    public BudgetAccount updateBudgetAccount(UUID budgetAccountId, UpdateBalanceBudgetAccountDto dto) {
        if(budgetAccountRepository.getBudgetAccountById(budgetAccountId)==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Budget Account not found");
        }

        return budgetAccountRepository.updateBudgetAccount(budgetAccountId, dto);
    }
}
