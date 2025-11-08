package com.example.spendwise.user;

import com.example.spendwise.application.dtos.user.UpdateUserNamesDto;
import com.example.spendwise.domain.entities.User;
import com.example.spendwise.infrastructure.repositories.user.SpringDataUserRepository;
import com.example.spendwise.infrastructure.repositories.user.UserRepositoryImpl;
import com.example.spendwise.infrastructure.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.spendwise.application.dtos.user.LoginUserDto;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {
    @Mock
    private SpringDataUserRepository springDataUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    private UserRepositoryImpl userRepository;
    private UUID testUserId;
    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepositoryImpl(springDataUserRepository, passwordEncoder, jwtUtil);
        testUserId = UUID.randomUUID();
        testUser = new User(testUserId, "Ion", "Popescu", "popescu@gmail.com", "parola1234");
    }

    @Test
    void createUser_Success() {
        when(springDataUserRepository.save(any(User.class))).thenReturn(testUser);

        User result = userRepository.createUser(testUser);

        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(springDataUserRepository).save(testUser);
    }

    @Test
    void existsByEmail_ReturnsTrue() {
        String email = "popescu@gmail.com";
        when(springDataUserRepository.existsByEmail(email)).thenReturn(true);

        boolean result = userRepository.existsByEmail(email);

        assertTrue(result);
        verify(springDataUserRepository).existsByEmail(email);
    }

    @Test
    void updateUserNames_Success() {
        UpdateUserNamesDto dto = new UpdateUserNamesDto();
        dto.setFirstname("Ion");
        dto.setLastname("Popescu");

        when(springDataUserRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(springDataUserRepository.save(any(User.class))).thenReturn(testUser);

        User result = userRepository.updateUserNames(testUserId, dto);

        assertNotNull(result);
        verify(springDataUserRepository).save(any(User.class));
    }

    @Test
    void updateUserNames_UserNotFound_ReturnsNull() {
        UpdateUserNamesDto dto = new UpdateUserNamesDto();
        when(springDataUserRepository.findById(testUserId)).thenReturn(Optional.empty());

        User result = userRepository.updateUserNames(testUserId, dto);

        assertNull(result);
        verify(springDataUserRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_Success() {
        when(springDataUserRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        User result = userRepository.getUserById(testUserId);

        assertNotNull(result);
        assertEquals(testUserId, result.getId());
    }

    @Test
    void getUserById_NotFound_ReturnsNull() {
        when(springDataUserRepository.findById(testUserId)).thenReturn(Optional.empty());

        User result = userRepository.getUserById(testUserId);

        assertNull(result);
    }

    @Test
    void updatePassword_Success() {
        String newPassword = "newHashedPassword";
        when(springDataUserRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(springDataUserRepository.save(any(User.class))).thenReturn(testUser);

        User result = userRepository.updatePassword(testUserId, newPassword);

        assertNotNull(result);
        verify(springDataUserRepository).save(any(User.class));
    }

    @Test
    void updatePassword_UserNotFound_ReturnsNull() {
        when(springDataUserRepository.findById(testUserId)).thenReturn(Optional.empty());

        User result = userRepository.updatePassword(testUserId, "newPassword");

        assertNull(result);
        verify(springDataUserRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_Success() {
        when(springDataUserRepository.existsById(testUserId)).thenReturn(true);
        doNothing().when(springDataUserRepository).deleteById(testUserId);

        boolean result = userRepository.deleteUser(testUserId);

        assertTrue(result);
        verify(springDataUserRepository).deleteById(testUserId);
    }

    @Test
    void deleteUser_NotFound_ReturnsFalse() {
        when(springDataUserRepository.existsById(testUserId)).thenReturn(false);

        boolean result = userRepository.deleteUser(testUserId);

        assertFalse(result);
        verify(springDataUserRepository, never()).deleteById(any());
    }

    @Test
    void loginUser_Success() {
        String email = "popescu@gmail.com";
        String rawPassword = "parola1234";
        String hashedPassword = "$2a$10$hashedPassword";
        String expectedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";

        testUser.setPassword(hashedPassword);
        when(springDataUserRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(true);
        when(jwtUtil.generateToken(testUserId.toString())).thenReturn(expectedToken);

        String result = userRepository.loginUser(email, rawPassword);

        assertNotNull(result);
        assertEquals(expectedToken, result);
        verify(passwordEncoder).matches(rawPassword, hashedPassword);
        verify(jwtUtil).generateToken(testUserId.toString());
    }

    @Test
    void loginUser_InvalidPassword_ReturnsNull() {
        String email = "popescu@gmail.com";
        String rawPassword = "wrongpassword";
        String hashedPassword = "$2a$10$hashedPassword";

        testUser.setPassword(hashedPassword);
        when(springDataUserRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(false);

        String result = userRepository.loginUser(email, rawPassword);

        assertNull(result);
        verify(passwordEncoder).matches(rawPassword, hashedPassword);
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void loginUser_UserNotFound_ReturnsNull() {
        String email = "nonexistent@gmail.com";
        String rawPassword = "parola1234";

        when(springDataUserRepository.findByEmail(email)).thenReturn(Optional.empty());

        String result = userRepository.loginUser(email, rawPassword);

        assertNull(result);
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void loginUser_NullPassword_ReturnsNull() {
        String email = "popescu@gmail.com";
        String rawPassword = "parola1234";

        testUser.setPassword(null);
        when(springDataUserRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        String result = userRepository.loginUser(email, rawPassword);

        assertNull(result);
        verify(passwordEncoder, never()).matches(any(), any());
    }

}
