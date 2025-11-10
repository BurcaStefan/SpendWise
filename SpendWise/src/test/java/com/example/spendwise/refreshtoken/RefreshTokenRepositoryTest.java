package com.example.spendwise.refreshtoken;

import com.example.spendwise.domain.entities.RefreshToken;
import com.example.spendwise.domain.entities.User;
import com.example.spendwise.infrastructure.repositories.refreshtoken.RefreshTokenRepositoryImpl;
import com.example.spendwise.infrastructure.repositories.refreshtoken.SpringDataRefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenRepositoryTest {

    @Mock
    private SpringDataRefreshTokenRepository springDataRefreshTokenRepository;

    private RefreshTokenRepositoryImpl refreshTokenRepository;
    private UUID testUserId;
    private RefreshToken testRefreshToken;

    @BeforeEach
    void setUp() {
        refreshTokenRepository = new RefreshTokenRepositoryImpl(springDataRefreshTokenRepository);
        testUserId = UUID.randomUUID();

        testRefreshToken = new RefreshToken();
        testRefreshToken.setTokenId(UUID.randomUUID());
        testRefreshToken.setUserId(testUserId);
        testRefreshToken.setToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTY3OCJ9.signature");
        testRefreshToken.setCreateDate(LocalDateTime.now());
        testRefreshToken.setExpirationDate(LocalDateTime.now().plusDays(30));
    }

    @Test
    void save_Success() {
        when(springDataRefreshTokenRepository.save(any(RefreshToken.class))).thenReturn(testRefreshToken);

        RefreshToken saved = refreshTokenRepository.save(testRefreshToken);

        assertNotNull(saved);
        assertEquals(testRefreshToken.getTokenId(), saved.getTokenId());
        assertEquals(testRefreshToken.getUserId(), saved.getUserId());
        assertEquals(testRefreshToken.getToken(), saved.getToken());
        verify(springDataRefreshTokenRepository).save(testRefreshToken);
    }

    @Test
    void save_UpdatesExistingToken() {
        RefreshToken updatedToken = new RefreshToken();
        updatedToken.setTokenId(testRefreshToken.getTokenId());
        updatedToken.setUserId(testRefreshToken.getUserId());
        updatedToken.setToken("new-token-value");
        updatedToken.setCreateDate(testRefreshToken.getCreateDate());
        updatedToken.setExpirationDate(testRefreshToken.getExpirationDate());

        when(springDataRefreshTokenRepository.save(any(RefreshToken.class))).thenReturn(updatedToken);

        RefreshToken updated = refreshTokenRepository.save(updatedToken);

        assertEquals(testRefreshToken.getTokenId(), updated.getTokenId());
        assertEquals("new-token-value", updated.getToken());
        verify(springDataRefreshTokenRepository).save(updatedToken);
    }

    @Test
    void findByUserId_Success() {
        when(springDataRefreshTokenRepository.findByUserId(testUserId)).thenReturn(Optional.of(testRefreshToken));

        Optional<RefreshToken> found = refreshTokenRepository.findByUserId(testUserId);

        assertTrue(found.isPresent());
        assertEquals(testRefreshToken.getTokenId(), found.get().getTokenId());
        assertEquals(testUserId, found.get().getUserId());
        verify(springDataRefreshTokenRepository).findByUserId(testUserId);
    }

    @Test
    void findByUserId_NotFound_ReturnsEmpty() {
        UUID nonExistentUserId = UUID.randomUUID();
        when(springDataRefreshTokenRepository.findByUserId(nonExistentUserId)).thenReturn(Optional.empty());

        Optional<RefreshToken> found = refreshTokenRepository.findByUserId(nonExistentUserId);

        assertTrue(found.isEmpty());
        verify(springDataRefreshTokenRepository).findByUserId(nonExistentUserId);
    }

    @Test
    void findByUserId_AfterDelete_ReturnsEmpty() {
        when(springDataRefreshTokenRepository.findByUserId(testUserId)).thenReturn(Optional.empty());
        doNothing().when(springDataRefreshTokenRepository).deleteByUserId(testUserId);

        refreshTokenRepository.deleteByUserId(testUserId);
        Optional<RefreshToken> found = refreshTokenRepository.findByUserId(testUserId);

        assertTrue(found.isEmpty());
        verify(springDataRefreshTokenRepository).deleteByUserId(testUserId);
    }

    @Test
    void deleteByUserId_Success() {
        doNothing().when(springDataRefreshTokenRepository).deleteByUserId(testUserId);
        when(springDataRefreshTokenRepository.findByUserId(testUserId)).thenReturn(Optional.empty());

        refreshTokenRepository.deleteByUserId(testUserId);
        Optional<RefreshToken> found = refreshTokenRepository.findByUserId(testUserId);

        assertTrue(found.isEmpty());
        verify(springDataRefreshTokenRepository).deleteByUserId(testUserId);
    }

    @Test
    void deleteByUserId_NonExistent_DoesNotThrow() {
        UUID nonExistentUserId = UUID.randomUUID();
        doNothing().when(springDataRefreshTokenRepository).deleteByUserId(nonExistentUserId);

        assertDoesNotThrow(() -> refreshTokenRepository.deleteByUserId(nonExistentUserId));
        verify(springDataRefreshTokenRepository).deleteByUserId(nonExistentUserId);
    }

    @Test
    void deleteByUserId_MultipleTimes_DoesNotThrow() {
        doNothing().when(springDataRefreshTokenRepository).deleteByUserId(testUserId);

        assertDoesNotThrow(() -> {
            refreshTokenRepository.deleteByUserId(testUserId);
            refreshTokenRepository.deleteByUserId(testUserId);
        });
        verify(springDataRefreshTokenRepository, times(2)).deleteByUserId(testUserId);
    }

    @Test
    void save_MultipleTokensForDifferentUsers_Success() {
        UUID userId2 = UUID.randomUUID();
        RefreshToken token2 = new RefreshToken();
        token2.setTokenId(UUID.randomUUID());
        token2.setUserId(userId2);
        token2.setToken("different-token");
        token2.setCreateDate(LocalDateTime.now());
        token2.setExpirationDate(LocalDateTime.now().plusDays(30));

        when(springDataRefreshTokenRepository.save(testRefreshToken)).thenReturn(testRefreshToken);
        when(springDataRefreshTokenRepository.save(token2)).thenReturn(token2);
        when(springDataRefreshTokenRepository.findByUserId(testUserId)).thenReturn(Optional.of(testRefreshToken));
        when(springDataRefreshTokenRepository.findByUserId(userId2)).thenReturn(Optional.of(token2));

        refreshTokenRepository.save(testRefreshToken);
        refreshTokenRepository.save(token2);

        Optional<RefreshToken> found1 = refreshTokenRepository.findByUserId(testUserId);
        Optional<RefreshToken> found2 = refreshTokenRepository.findByUserId(userId2);

        assertTrue(found1.isPresent());
        assertTrue(found2.isPresent());
        assertNotEquals(found1.get().getTokenId(), found2.get().getTokenId());
    }

    @Test
    void save_WithExpiredDate_Success() {
        testRefreshToken.setExpirationDate(LocalDateTime.now().minusDays(1));
        when(springDataRefreshTokenRepository.save(testRefreshToken)).thenReturn(testRefreshToken);

        RefreshToken saved = refreshTokenRepository.save(testRefreshToken);

        assertNotNull(saved);
        assertTrue(saved.getExpirationDate().isBefore(LocalDateTime.now()));
    }

    @Test
    void findByUserId_ReturnsTokenWithCorrectDates() {
        LocalDateTime createDate = LocalDateTime.now();
        LocalDateTime expirationDate = createDate.plusDays(30);

        testRefreshToken.setCreateDate(createDate);
        testRefreshToken.setExpirationDate(expirationDate);
        when(springDataRefreshTokenRepository.findByUserId(testUserId)).thenReturn(Optional.of(testRefreshToken));

        Optional<RefreshToken> found = refreshTokenRepository.findByUserId(testUserId);

        assertTrue(found.isPresent());
        assertNotNull(found.get().getCreateDate());
        assertNotNull(found.get().getExpirationDate());
    }

    @Test
    void deleteByUserId_OnlyDeletesSpecificUser() {
        UUID userId2 = UUID.randomUUID();
        RefreshToken token2 = new RefreshToken();
        token2.setTokenId(UUID.randomUUID());
        token2.setUserId(userId2);
        token2.setToken("different-token");

        doNothing().when(springDataRefreshTokenRepository).deleteByUserId(testUserId);
        when(springDataRefreshTokenRepository.findByUserId(testUserId)).thenReturn(Optional.empty());
        when(springDataRefreshTokenRepository.findByUserId(userId2)).thenReturn(Optional.of(token2));

        refreshTokenRepository.deleteByUserId(testUserId);

        Optional<RefreshToken> found1 = refreshTokenRepository.findByUserId(testUserId);
        Optional<RefreshToken> found2 = refreshTokenRepository.findByUserId(userId2);

        assertTrue(found1.isEmpty());
        assertTrue(found2.isPresent());
    }
}
