package com.example.spendwise.budgetaccount;

import com.example.spendwise.application.dtos.budgetaccount.CreateBudgetAccountDto;
import com.example.spendwise.application.dtos.budgetaccount.GetBudgetAccountByIdDto;
import com.example.spendwise.application.dtos.budgetaccount.UpdateBalanceBudgetAccountDto;
import com.example.spendwise.application.services.BudgetAccountServices;
import com.example.spendwise.domain.entities.BudgetAccount;
import com.example.spendwise.domain.factory.EntityFactory;
import com.example.spendwise.domain.repositories.IBudgetAccountRepository;
import com.example.spendwise.domain.repositories.IUserRepository;
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
class BudgetAccountServicesTest {
    @Mock
    private IBudgetAccountRepository budgetAccountRepository;
    @Mock
    private EntityFactory<BudgetAccount, CreateBudgetAccountDto> budgetAccountFactory;
    @Mock
    private IUserRepository userRepository;

    private BudgetAccountServices budgetAccountServices;
    private UUID testAccountId;
    private UUID testUserId;
    private BudgetAccount testBudgetAccount;

    @BeforeEach
    void setUp() {
        budgetAccountServices = new BudgetAccountServices(budgetAccountRepository, budgetAccountFactory, userRepository);
        testAccountId = UUID.randomUUID();
        testUserId = UUID.randomUUID();
        testBudgetAccount = new BudgetAccount(testAccountId, testUserId, 1000.00);
    }

    @Test
    void createBudgetAccount_Success() {
        CreateBudgetAccountDto dto = new CreateBudgetAccountDto();
        dto.setUserId(testUserId);

        when(userRepository.getUserById(testUserId)).thenReturn(new com.example.spendwise.domain.entities.User());
        when(budgetAccountFactory.create(dto)).thenReturn(testBudgetAccount);
        when(budgetAccountRepository.createBudgetAccount(testBudgetAccount)).thenReturn(testBudgetAccount);

        BudgetAccount result = budgetAccountServices.createBudgetAccount(dto);

        assertNotNull(result);
        assertEquals(testBudgetAccount.getValue(), result.getValue());
        verify(userRepository).getUserById(testUserId);
        verify(budgetAccountRepository).createBudgetAccount(testBudgetAccount);
    }

    @Test
    void createBudgetAccount_UserNotFound_ThrowsException() {
        CreateBudgetAccountDto dto = new CreateBudgetAccountDto();
        dto.setUserId(testUserId);

        when(userRepository.getUserById(testUserId)).thenReturn(null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> budgetAccountServices.createBudgetAccount(dto));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("User not found", ex.getReason());
    }

    @Test
    void getBudgetAccountById_Success() {
        GetBudgetAccountByIdDto dto = new GetBudgetAccountByIdDto();
        dto.setBudgetAccountId(testAccountId);

        when(budgetAccountRepository.getBudgetAccountById(testAccountId)).thenReturn(dto);

        GetBudgetAccountByIdDto result = budgetAccountServices.getBudgetAccountById(testAccountId);

        assertNotNull(result);
        assertEquals(testAccountId, result.getBudgetAccountId());
    }


    @Test
    void getBudgetAccountById_NotFound_ThrowsException() {
        when(budgetAccountRepository.getBudgetAccountById(testAccountId)).thenReturn(null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> budgetAccountServices.getBudgetAccountById(testAccountId));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Budget Account not found", ex.getReason());
    }

    @Test
    void updateBudgetAccount_Success() {
        UpdateBalanceBudgetAccountDto dto = new UpdateBalanceBudgetAccountDto();
        dto.setValue(2000.00);

        GetBudgetAccountByIdDto getDto = new GetBudgetAccountByIdDto();
        getDto.setBudgetAccountId(testAccountId);

        BudgetAccount updatedAccount = new BudgetAccount(testAccountId, testUserId, 2000.00);

        when(budgetAccountRepository.getBudgetAccountById(testAccountId)).thenReturn(getDto);
        when(budgetAccountRepository.updateBudgetAccount(testAccountId, dto)).thenReturn(updatedAccount);

        BudgetAccount result = budgetAccountServices.updateBudgetAccount(testAccountId, dto);

        assertNotNull(result);
        assertEquals(2000.00, result.getValue());
        verify(budgetAccountRepository).updateBudgetAccount(testAccountId, dto);
    }

    @Test
    void updateBudgetAccount_NotFound_ThrowsException() {
        UpdateBalanceBudgetAccountDto dto = new UpdateBalanceBudgetAccountDto();

        when(budgetAccountRepository.getBudgetAccountById(testAccountId)).thenReturn(null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> budgetAccountServices.updateBudgetAccount(testAccountId, dto));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void deleteBudgetAccount_Success() {
        GetBudgetAccountByIdDto dto = new GetBudgetAccountByIdDto();
        dto.setBudgetAccountId(testAccountId);

        when(budgetAccountRepository.getBudgetAccountById(testAccountId)).thenReturn(dto);
        when(budgetAccountRepository.deleteBudgetAccount(testAccountId)).thenReturn(true);

        boolean result = budgetAccountServices.deleteBudgetAccount(testAccountId);

        assertTrue(result);
        verify(budgetAccountRepository).deleteBudgetAccount(testAccountId);
    }

    @Test
    void deleteBudgetAccount_NotFound_ThrowsException() {
        when(budgetAccountRepository.getBudgetAccountById(testAccountId)).thenReturn(null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> budgetAccountServices.deleteBudgetAccount(testAccountId));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}
