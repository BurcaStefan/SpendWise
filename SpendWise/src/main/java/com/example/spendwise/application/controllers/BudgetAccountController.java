package com.example.spendwise.application.controllers;

import com.example.spendwise.application.dtos.budgetaccount.CreateBudgetAccountDto;
import com.example.spendwise.application.services.BudgetAccountServices;
import com.example.spendwise.domain.entities.BudgetAccount;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/budget-accounts")
public class BudgetAccountController {
    private final BudgetAccountServices budgetAccountServices;
    public BudgetAccountController(BudgetAccountServices budgetAccountServices) {
        this.budgetAccountServices = budgetAccountServices;
    }

    @PostMapping
    public ResponseEntity<BudgetAccount> createBudgetAccount(@Valid @RequestBody CreateBudgetAccountDto dto) {
        BudgetAccount createdBudgetAccount = budgetAccountServices.createBudgetAccount(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBudgetAccount);
    }
}
