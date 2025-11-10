package com.example.spendwise.refreshtoken;

import com.example.spendwise.application.dtos.refreshtoken.CreateRefreshTokenDto;
import com.example.spendwise.application.services.RefreshTokenServices;
import com.example.spendwise.domain.entities.RefreshToken;
import com.example.spendwise.domain.entities.User;
import com.example.spendwise.domain.factory.EntityFactory;
import com.example.spendwise.domain.repositories.IRefreshTokenRepository;
import com.example.spendwise.domain.repositories.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServicesTest {

    @Mock
    private IRefreshTokenRepository refreshTokenRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private EntityFactory<RefreshToken, CreateRefreshTokenDto> refreshTokenFactory;

    private RefreshTokenServices refreshTokenServices;
    private UUID testUserId;
    private UUID testTokenId;
    private User testUser;
    private RefreshToken testRefreshToken;
    private CreateRefreshTokenDto createDto;

    @BeforeEach
    void setUp() {
        refreshTokenServices = new RefreshTokenServices(
                refreshTokenRepository,
                userRepository,
                refreshTokenFactory
        );

        testUserId = UUID.randomUUID();
        testTokenId = UUID.randomUUID();

        testUser = new User(
                testUserId,
                "Ion",
                "Popescu",
                "popescu@gmail.com",
                "parola1234"
        );

        testRefreshToken = new RefreshToken();
        testRefreshToken.setTokenId(testTokenId);
        testRefreshToken.setUserId(testUserId);
        testRefreshToken.setToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTY3OCIsInR5cGUiOiJyZWZyZXNoIn0.signature");
        testRefreshToken.setCreateDate(LocalDateTime.now());
        testRefreshToken.setExpirationDate(LocalDateTime.now().plusDays(30));

        createDto = new CreateRefreshTokenDto();
        createDto.setUserId(testUserId);
    }

    @Test
    void create_Success_NewToken() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(refreshTokenRepository.findByUserId(testUserId)).thenReturn(Optional.empty());
        when(refreshTokenFactory.create(createDto)).thenReturn(testRefreshToken);
        when(refreshTokenRepository.save(testRefreshToken)).thenReturn(testRefreshToken);

        RefreshToken result = refreshTokenServices.create(createDto);

        assertNotNull(result);
        assertEquals(testTokenId, result.getTokenId());
        assertEquals(testUserId, result.getUserId());
        assertNotNull(result.getToken());
        verify(userRepository).findById(testUserId);
        verify(refreshTokenRepository).findByUserId(testUserId);
        verify(refreshTokenFactory).create(createDto);
        verify(refreshTokenRepository).save(testRefreshToken);
        verify(refreshTokenRepository, never()).deleteByUserId(any());
    }

    @Test
    void create_Success_ReplacesExistingToken() {
        RefreshToken existingToken = new RefreshToken();
        existingToken.setTokenId(UUID.randomUUID());
        existingToken.setUserId(testUserId);

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(refreshTokenRepository.findByUserId(testUserId)).thenReturn(Optional.of(existingToken));
        when(refreshTokenFactory.create(createDto)).thenReturn(testRefreshToken);
        when(refreshTokenRepository.save(testRefreshToken)).thenReturn(testRefreshToken);

        RefreshToken result = refreshTokenServices.create(createDto);

        assertNotNull(result);
        assertEquals(testTokenId, result.getTokenId());
        assertEquals(testUserId, result.getUserId());
        verify(refreshTokenRepository).deleteByUserId(testUserId);
        verify(refreshTokenRepository).save(testRefreshToken);
    }

    @Test
    void create_UserNotFound_ThrowsException() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> refreshTokenServices.create(createDto)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason().contains("User not found"));
        verify(userRepository).findById(testUserId);
        verify(refreshTokenRepository, never()).findByUserId(any());
        verify(refreshTokenFactory, never()).create(any());
        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    void create_NullUserId_ThrowsException() {
        CreateRefreshTokenDto dtoWithNullUserId = new CreateRefreshTokenDto();
        dtoWithNullUserId.setUserId(null);

        when(userRepository.findById(null)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> refreshTokenServices.create(dtoWithNullUserId)
        );
    }

    @Test
    void create_SaveFails_PropagatesException() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(refreshTokenRepository.findByUserId(testUserId)).thenReturn(Optional.empty());
        when(refreshTokenFactory.create(createDto)).thenReturn(testRefreshToken);
        when(refreshTokenRepository.save(testRefreshToken))
                .thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class,
                () -> refreshTokenServices.create(createDto)
        );

        verify(refreshTokenRepository).save(testRefreshToken);
    }

    @Test
    void create_DeleteExistingTokenFails_PropagatesException() {
        RefreshToken existingToken = new RefreshToken();
        existingToken.setUserId(testUserId);

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(refreshTokenRepository.findByUserId(testUserId)).thenReturn(Optional.of(existingToken));
        doThrow(new RuntimeException("Delete failed"))
                .when(refreshTokenRepository).deleteByUserId(testUserId);

        assertThrows(RuntimeException.class,
                () -> refreshTokenServices.create(createDto)
        );

        verify(refreshTokenRepository).deleteByUserId(testUserId);
        verify(refreshTokenFactory, never()).create(any());
    }

    @Test
    void create_FactoryFails_PropagatesException() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(refreshTokenRepository.findByUserId(testUserId)).thenReturn(Optional.empty());
        when(refreshTokenFactory.create(createDto))
                .thenThrow(new RuntimeException("Factory error"));

        assertThrows(RuntimeException.class,
                () -> refreshTokenServices.create(createDto)
        );

        verify(refreshTokenFactory).create(createDto);
        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    void create_WithExpiredToken_ReplacesSuccessfully() {
        RefreshToken expiredToken = new RefreshToken();
        expiredToken.setUserId(testUserId);
        expiredToken.setExpirationDate(LocalDateTime.now().minusDays(1));

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(refreshTokenRepository.findByUserId(testUserId)).thenReturn(Optional.of(expiredToken));
        when(refreshTokenFactory.create(createDto)).thenReturn(testRefreshToken);
        when(refreshTokenRepository.save(testRefreshToken)).thenReturn(testRefreshToken);

        RefreshToken result = refreshTokenServices.create(createDto);

        assertNotNull(result);
        assertTrue(result.getExpirationDate().isAfter(LocalDateTime.now()));
        verify(refreshTokenRepository).deleteByUserId(testUserId);
    }

    @Test
    void getByUserId_Success() {
        when(refreshTokenRepository.findByUserId(testUserId)).thenReturn(Optional.of(testRefreshToken));

        RefreshToken result = refreshTokenServices.getByUserId(testUserId);

        assertNotNull(result);
        assertEquals(testTokenId, result.getTokenId());
        assertEquals(testUserId, result.getUserId());
        assertEquals(testRefreshToken.getToken(), result.getToken());
        verify(refreshTokenRepository).findByUserId(testUserId);
    }

    @Test
    void getByUserId_NotFound_ThrowsException() {
        when(refreshTokenRepository.findByUserId(testUserId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> refreshTokenServices.getByUserId(testUserId)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Refresh token not found"));
        verify(refreshTokenRepository).findByUserId(testUserId);
    }

    @Test
    void getByUserId_NullUserId_ThrowsException() {
        when(refreshTokenRepository.findByUserId(null)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> refreshTokenServices.getByUserId(null)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void getByUserId_DifferentUserIds_ReturnsCorrectToken() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        RefreshToken token1 = new RefreshToken();
        token1.setUserId(userId1);
        token1.setTokenId(UUID.randomUUID());

        RefreshToken token2 = new RefreshToken();
        token2.setUserId(userId2);
        token2.setTokenId(UUID.randomUUID());

        when(refreshTokenRepository.findByUserId(userId1)).thenReturn(Optional.of(token1));
        when(refreshTokenRepository.findByUserId(userId2)).thenReturn(Optional.of(token2));

        RefreshToken result1 = refreshTokenServices.getByUserId(userId1);
        RefreshToken result2 = refreshTokenServices.getByUserId(userId2);

        assertEquals(userId1, result1.getUserId());
        assertEquals(userId2, result2.getUserId());
        assertNotEquals(result1.getTokenId(), result2.getTokenId());
    }
}
