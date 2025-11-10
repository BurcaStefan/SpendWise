package com.example.spendwise.infrastructure.repositories.refreshtoken;

import com.example.spendwise.domain.entities.RefreshToken;
import com.example.spendwise.domain.repositories.IRefreshTokenRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public class RefreshTokenRepositoryImpl implements IRefreshTokenRepository {

    private final SpringDataRefreshTokenRepository refreshTokenRepository;

    public RefreshTokenRepositoryImpl(SpringDataRefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public Optional<RefreshToken> findByUserId(UUID userId) {
        return refreshTokenRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteByUserId(UUID userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }
}
