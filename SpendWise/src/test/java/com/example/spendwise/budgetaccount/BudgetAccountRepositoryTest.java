package com.example.spendwise.budgetaccount;

import com.example.spendwise.application.dtos.budgetaccount.GetBudgetAccountByIdDto;
import com.example.spendwise.application.dtos.budgetaccount.UpdateBalanceBudgetAccountDto;
import com.example.spendwise.domain.entities.BudgetAccount;
import com.example.spendwise.infrastructure.repositories.budgetaccount.BudgetAccountRepository;
import com.example.spendwise.infrastructure.repositories.budgetaccount.SpringDataBudgetAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetAccountRepositoryTest {
    @Mock
    private SpringDataBudgetAccountRepository springDataBudgetAccountRepository;

    private BudgetAccountRepository budgetAccountRepository;
    private UUID testAccountId;
    private UUID testUserId;
    private BudgetAccount testBudgetAccount;

    @BeforeEach
    void setUp() {
        budgetAccountRepository = new BudgetAccountRepository(springDataBudgetAccountRepository);
        testAccountId = UUID.randomUUID();
        testUserId = UUID.randomUUID();
        testBudgetAccount = new BudgetAccount(testAccountId, testUserId, 1000.00);
    }

    @Test
    void createBudgetAccount_Success() {
        when(springDataBudgetAccountRepository.save(any(BudgetAccount.class))).thenReturn(testBudgetAccount);

        BudgetAccount result = budgetAccountRepository.createBudgetAccount(testBudgetAccount);

        assertNotNull(result);
        assertEquals(testBudgetAccount.getValue(), result.getValue());
        verify(springDataBudgetAccountRepository).save(testBudgetAccount);
    }

    @Test
    void getBudgetAccountById_Success() {
        when(springDataBudgetAccountRepository.findById(testAccountId)).thenReturn(Optional.of(testBudgetAccount));

        GetBudgetAccountByIdDto result = budgetAccountRepository.getBudgetAccountById(testAccountId);

        assertNotNull(result);
        assertEquals(testAccountId, result.getBudgetAccountId());
        assertEquals(testUserId, result.getUserId());
        assertEquals(1000.00, result.getValue());
    }

    @Test
    void getBudgetAccountById_NotFound_ReturnsNull() {
        when(springDataBudgetAccountRepository.findById(testAccountId)).thenReturn(Optional.empty());

        GetBudgetAccountByIdDto result = budgetAccountRepository.getBudgetAccountById(testAccountId);

        assertNull(result);
    }

    @Test
    void updateBudgetAccount_Success() {
        UpdateBalanceBudgetAccountDto dto = new UpdateBalanceBudgetAccountDto();
        dto.setValue(2000.00);

        BudgetAccount updatedAccount = new BudgetAccount(testAccountId, testUserId,2000.00);

        when(springDataBudgetAccountRepository.findById(testAccountId)).thenReturn(Optional.of(testBudgetAccount));
        when(springDataBudgetAccountRepository.save(any(BudgetAccount.class))).thenReturn(updatedAccount);

        BudgetAccount result = budgetAccountRepository.updateBudgetAccount(testAccountId, dto);

        assertNotNull(result);
        assertEquals(2000.00, result.getValue());
        verify(springDataBudgetAccountRepository).save(any(BudgetAccount.class));
    }

    @Test
    void updateBudgetAccount_NotFound_ReturnsNull() {
        UpdateBalanceBudgetAccountDto dto = new UpdateBalanceBudgetAccountDto();

        when(springDataBudgetAccountRepository.findById(testAccountId)).thenReturn(Optional.empty());

        BudgetAccount result = budgetAccountRepository.updateBudgetAccount(testAccountId, dto);

        assertNull(result);
        verify(springDataBudgetAccountRepository, never()).save(any(BudgetAccount.class));
    }

    @Test
    void deleteBudgetAccount_Success() {
        when(springDataBudgetAccountRepository.existsById(testAccountId)).thenReturn(true);
        doNothing().when(springDataBudgetAccountRepository).deleteById(testAccountId);

        boolean result = budgetAccountRepository.deleteBudgetAccount(testAccountId);

        assertTrue(result);
        verify(springDataBudgetAccountRepository).deleteById(testAccountId);
    }

    @Test
    void deleteBudgetAccount_NotFound_ReturnsFalse() {
        when(springDataBudgetAccountRepository.existsById(testAccountId)).thenReturn(false);

        boolean result = budgetAccountRepository.deleteBudgetAccount(testAccountId);

        assertFalse(result);
        verify(springDataBudgetAccountRepository, never()).deleteById(any());
    }
}
