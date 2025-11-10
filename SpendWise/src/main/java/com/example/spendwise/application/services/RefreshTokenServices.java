package com.example.spendwise.application.services;

import com.example.spendwise.application.dtos.refreshtoken.CreateRefreshTokenDto;
import com.example.spendwise.domain.entities.RefreshToken;
import com.example.spendwise.domain.factory.EntityFactory;
import com.example.spendwise.domain.repositories.IRefreshTokenRepository;
import com.example.spendwise.domain.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class RefreshTokenServices {
    private final IRefreshTokenRepository refreshTokenRepository;
    private final IUserRepository userRepository;
    private final EntityFactory<RefreshToken, CreateRefreshTokenDto> refreshTokenFactory;

    public RefreshTokenServices(IRefreshTokenRepository refreshTokenRepository,
                               IUserRepository userRepository,
                               @Qualifier("refreshTokenFactory") EntityFactory<RefreshToken, CreateRefreshTokenDto> refreshTokenFactory) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.refreshTokenFactory = refreshTokenFactory;
    }

    @Transactional
    public RefreshToken create(CreateRefreshTokenDto dto) {
        userRepository.findById(dto.getUserId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + dto.getUserId()));

        refreshTokenRepository.findByUserId(dto.getUserId())
                .ifPresent(existingToken -> refreshTokenRepository.deleteByUserId(dto.getUserId()));

        RefreshToken newToken = refreshTokenFactory.create(dto);
        return refreshTokenRepository.save(newToken);
    }

    public RefreshToken getByUserId(UUID userId) {
        return refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Refresh token not found for user: " + userId
                ));
    }
}
