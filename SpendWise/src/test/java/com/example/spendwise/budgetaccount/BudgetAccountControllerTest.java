package com.example.spendwise.budgetaccount;

import com.example.spendwise.application.dtos.budgetaccount.CreateBudgetAccountDto;
import com.example.spendwise.application.dtos.budgetaccount.GetBudgetAccountByIdDto;
import com.example.spendwise.application.dtos.budgetaccount.UpdateBalanceBudgetAccountDto;
import com.example.spendwise.application.services.BudgetAccountServices;
import com.example.spendwise.controllers.BudgetAccountController;
import com.example.spendwise.domain.entities.BudgetAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BudgetAccountControllerTest {
    @Mock
    private BudgetAccountServices budgetAccountServices;

    private BudgetAccountController budgetAccountController;
    private UUID testAccountId;
    private UUID testUserId;
    private BudgetAccount testBudgetAccount;

    @BeforeEach
    void setUp() {
        budgetAccountController = new BudgetAccountController(budgetAccountServices);
        testAccountId = UUID.randomUUID();
        testUserId = UUID.randomUUID();
        testBudgetAccount = new BudgetAccount(testAccountId, testUserId, 1000.00);
    }

    @Test
    void createBudgetAccount_Success() {
        CreateBudgetAccountDto dto = new CreateBudgetAccountDto();
        dto.setUserId(testUserId);

        when(budgetAccountServices.createBudgetAccount(dto)).thenReturn(testBudgetAccount);

        var response = budgetAccountController.createBudgetAccount(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testBudgetAccount, response.getBody());
        verify(budgetAccountServices).createBudgetAccount(dto);
    }

    @Test
    void createBudgetAccount_UserNotFound_ThrowsException() {
        CreateBudgetAccountDto dto = new CreateBudgetAccountDto();
        dto.setUserId(testUserId);

        when(budgetAccountServices.createBudgetAccount(dto))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> budgetAccountController.createBudgetAccount(dto));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void getBudgetAccountById_Success() {
        GetBudgetAccountByIdDto dto = new GetBudgetAccountByIdDto();
        dto.setBudgetAccountId(testAccountId);
        dto.setUserId(testUserId);
        dto.setValue(1000.00);

        when(budgetAccountServices.getBudgetAccountById(testAccountId)).thenReturn(dto);

        var response = budgetAccountController.getBudgetAccountById(testAccountId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(budgetAccountServices).getBudgetAccountById(testAccountId);
    }

    @Test
    void getBudgetAccountById_NotFound_ThrowsException() {
        when(budgetAccountServices.getBudgetAccountById(testAccountId))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Budget Account not found"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> budgetAccountController.getBudgetAccountById(testAccountId));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void updateBudgetAccount_Success() {
        UpdateBalanceBudgetAccountDto dto = new UpdateBalanceBudgetAccountDto();
        dto.setValue(2000.00);

        BudgetAccount updatedAccount = new BudgetAccount(testAccountId, testUserId, 2000.00);
        when(budgetAccountServices.updateBudgetAccount(testAccountId, dto)).thenReturn(updatedAccount);

        var response = budgetAccountController.updateBudgetAccount(testAccountId, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedAccount, response.getBody());
        verify(budgetAccountServices).updateBudgetAccount(testAccountId, dto);
    }

    @Test
    void updateBudgetAccount_NotFound_ThrowsException() {
        UpdateBalanceBudgetAccountDto dto = new UpdateBalanceBudgetAccountDto();

        when(budgetAccountServices.updateBudgetAccount(testAccountId, dto))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Budget Account not found"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> budgetAccountController.updateBudgetAccount(testAccountId, dto));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void deleteBudgetAccount_Success() {
        when(budgetAccountServices.deleteBudgetAccount(testAccountId)).thenReturn(true);

        var response = budgetAccountController.deleteBudgetAccount(testAccountId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertTrue(response.getBody());
        verify(budgetAccountServices).deleteBudgetAccount(testAccountId);
    }

    @Test
    void deleteBudgetAccount_NotFound() {
        when(budgetAccountServices.deleteBudgetAccount(testAccountId)).thenReturn(false);

        var response = budgetAccountController.deleteBudgetAccount(testAccountId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody());
    }
}
