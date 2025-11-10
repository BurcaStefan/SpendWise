package com.example.spendwise.refreshtoken;

import com.example.spendwise.application.dtos.refreshtoken.CreateRefreshTokenDto;
import com.example.spendwise.application.services.RefreshTokenServices;
import com.example.spendwise.controllers.RefreshTokenController;
import com.example.spendwise.domain.entities.RefreshToken;
import com.example.spendwise.infrastructure.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RefreshTokenServices refreshTokenServices;

    @InjectMocks
    private RefreshTokenController refreshTokenController;

    private ObjectMapper objectMapper;
    private UUID testUserId;
    private UUID testTokenId;
    private RefreshToken testRefreshToken;
    private CreateRefreshTokenDto createDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(refreshTokenController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        testUserId = UUID.randomUUID();
        testTokenId = UUID.randomUUID();

        testRefreshToken = new RefreshToken();
        testRefreshToken.setTokenId(testTokenId);
        testRefreshToken.setUserId(testUserId);
        testRefreshToken.setToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTY3OCJ9.signature");
        testRefreshToken.setCreateDate(LocalDateTime.now());
        testRefreshToken.setExpirationDate(LocalDateTime.now().plusDays(30));

        createDto = new CreateRefreshTokenDto();
        createDto.setUserId(testUserId);
    }

    @Test
    void createRefreshToken_Success() throws Exception {
        when(refreshTokenServices.create(any(CreateRefreshTokenDto.class))).thenReturn(testRefreshToken);

        mockMvc.perform(post("/api/refresh-tokens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.refreshToken").value(testRefreshToken.getToken()));

        verify(refreshTokenServices).create(any(CreateRefreshTokenDto.class));
    }

    @Test
    void createRefreshToken_UserNotFound_ReturnsNotFound() throws Exception {
        when(refreshTokenServices.create(any(CreateRefreshTokenDto.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        mockMvc.perform(post("/api/refresh-tokens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isNotFound());

        verify(refreshTokenServices).create(any(CreateRefreshTokenDto.class));
    }

    @Test
    void createRefreshToken_ServiceException_ReturnsInternalServerError() throws Exception {
        when(refreshTokenServices.create(any(CreateRefreshTokenDto.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Service error"));

        mockMvc.perform(post("/api/refresh-tokens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Service error"));

        verify(refreshTokenServices).create(any(CreateRefreshTokenDto.class));
    }

    @Test
    void getByUserId_Success() throws Exception {
        when(refreshTokenServices.getByUserId(testUserId)).thenReturn(testRefreshToken);

        mockMvc.perform(get("/api/refresh-tokens/{userId}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenId").value(testTokenId.toString()))
                .andExpect(jsonPath("$.userId").value(testUserId.toString()));

        verify(refreshTokenServices).getByUserId(testUserId);
    }

    @Test
    void getByUserId_NotFound_ReturnsNotFound() throws Exception {
        when(refreshTokenServices.getByUserId(testUserId))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Refresh token not found"));

        mockMvc.perform(get("/api/refresh-tokens/{userId}", testUserId))
                .andExpect(status().isNotFound());

        verify(refreshTokenServices).getByUserId(testUserId);
    }

    @Test
    void getByUserId_ServiceException_ReturnsInternalServerError() throws Exception {
        when(refreshTokenServices.getByUserId(testUserId))
                .thenThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Service error"));

        mockMvc.perform(get("/api/refresh-tokens/{userId}", testUserId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Service error"));

        verify(refreshTokenServices).getByUserId(testUserId);
    }
}