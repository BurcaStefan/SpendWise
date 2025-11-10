package com.example.spendwise.controllers;

import com.example.spendwise.application.dtos.refreshtoken.CreateRefreshTokenDto;
import com.example.spendwise.application.services.RefreshTokenServices;
import com.example.spendwise.domain.entities.RefreshToken;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/refresh-tokens")
public class RefreshTokenController {

    private final RefreshTokenServices refreshTokenServices;

    public RefreshTokenController(RefreshTokenServices refreshTokenServices) {
        this.refreshTokenServices = refreshTokenServices;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createRefreshToken(@Valid @RequestBody CreateRefreshTokenDto dto) {
        RefreshToken createdToken = refreshTokenServices.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("refreshToken", createdToken.getToken()));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<RefreshToken> getByUserId(@PathVariable UUID userId) {
        RefreshToken token = refreshTokenServices.getByUserId(userId);
        return ResponseEntity.ok(token);
    }
}