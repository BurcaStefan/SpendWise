package com.example.spendwise.user;

import com.example.spendwise.application.dtos.user.UpdateUserNamesDto;
import com.example.spendwise.domain.entities.User;
import com.example.spendwise.infrastructure.repositories.user.SpringDataUserRepository;
import com.example.spendwise.infrastructure.repositories.user.UserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {
    @Mock
    private SpringDataUserRepository springDataUserRepository;

    private UserRepositoryImpl userRepository;
    private UUID testUserId;
    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepositoryImpl(springDataUserRepository);
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
}
