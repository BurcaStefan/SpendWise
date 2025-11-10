package com.example.spendwise.domain.factory;

import com.example.spendwise.application.dtos.refreshtoken.CreateRefreshTokenDto;
import com.example.spendwise.domain.entities.RefreshToken;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Component("refreshTokenFactory")
public class RefreshTokenFactory implements EntityFactory<RefreshToken, CreateRefreshTokenDto> {

    @Value("${jwt.refresh.secret}")
    private String refreshSecret;

    @Value("${jwt.refresh.expiration}")
    private long refreshExpirationMs;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(refreshSecret.getBytes());
    }

    @Override
    public RefreshToken create(CreateRefreshTokenDto dto) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiration = now.plus(Duration.ofMillis(refreshExpirationMs));

        Date issuedAt = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        Date expiresAt = Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant());

        String tokenString = Jwts.builder()
                .setSubject(dto.getUserId().toString())
                .setIssuedAt(issuedAt)
                .setExpiration(expiresAt)
                .claim("type", "refresh")
                .claim("tokenId", UUID.randomUUID().toString())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenId(UUID.randomUUID());
        refreshToken.setUserId(dto.getUserId());
        refreshToken.setToken(tokenString);
        refreshToken.setCreateDate(now);
        refreshToken.setExpirationDate(expiration);

        return refreshToken;
    }
}
