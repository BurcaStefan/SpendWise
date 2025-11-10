package com.example.spendwise.application.dtos.refreshtoken;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class CreateRefreshTokenDto {

    @NotNull
    private UUID userId;

    public CreateRefreshTokenDto() {}

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
