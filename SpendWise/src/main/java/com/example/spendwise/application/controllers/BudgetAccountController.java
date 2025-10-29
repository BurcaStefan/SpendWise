package com.example.spendwise.application.controllers;

import com.example.spendwise.application.dtos.budgetaccount.CreateBudgetAccountDto;
import com.example.spendwise.application.dtos.budgetaccount.GetBudgetAccountByIdDto;
import com.example.spendwise.application.services.BudgetAccountServices;
import com.example.spendwise.domain.entities.BudgetAccount;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

    @GetMapping("/{accountId}")
    public ResponseEntity<GetBudgetAccountByIdDto> getBudgetAccountById(@PathVariable UUID accountId) {
        GetBudgetAccountByIdDto budgetAccount = budgetAccountServices.getBudgetAccountById(accountId);
        return ResponseEntity.ok(budgetAccount);
    }
}
