package com.example.spendwise.domain.repositories;

import com.example.spendwise.domain.entities.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface IRefreshTokenRepository {
    Optional<RefreshToken> findByUserId(UUID userId);
    void deleteByUserId(UUID userId);
    RefreshToken save(RefreshToken refreshToken);
}
