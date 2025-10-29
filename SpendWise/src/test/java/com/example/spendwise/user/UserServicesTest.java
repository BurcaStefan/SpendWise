package com.example.spendwise.user;

import com.example.spendwise.application.dtos.user.CreateUserDto;
import com.example.spendwise.application.dtos.user.UpdateUserNamesDto;
import com.example.spendwise.application.dtos.user.UpdateUserPasswordDto;
import com.example.spendwise.application.factory.EntityFactory;
import com.example.spendwise.application.services.UserServices;
import com.example.spendwise.domain.entities.User;
import com.example.spendwise.domain.repositories.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServicesTest {
    @Mock
    private IUserRepository userRepository;
    @Mock
    private EntityFactory<User, CreateUserDto> userFactory;
    @Mock
    private PasswordEncoder passwordEncoder;

    private UserServices userServices;
    private UUID testUserId;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userServices = new UserServices(userRepository, userFactory, passwordEncoder);
        testUserId = UUID.randomUUID();
        testUser = new User(testUserId, "Ion", "Popescu", "popescu@gmail.com", "parola1234");
    }

    @Test
    void createUser_Success() {
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail("popescu@gmail.com");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(userFactory.create(dto)).thenReturn(testUser);
        when(userRepository.createUser(testUser)).thenReturn(testUser);

        User result = userServices.createUser(dto);

        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
    }

    @Test
    void createUser_EmailExists_ThrowsException() {
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail("ion@gmail.com");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> userServices.createUser(dto));
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        User result = userServices.getUserById(testUserId);

        assertNotNull(result);
        assertEquals(testUserId, result.getId());
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userServices.getUserById(testUserId));
    }

    @Test
    void updateUserNames_Success() {
        UpdateUserNamesDto dto = new UpdateUserNamesDto();
        dto.setFirstname("Ionut");
        dto.setLastname("Popescu");

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.updateUserNames(testUserId, dto)).thenReturn(testUser);

        User result = userServices.updateUserNames(testUserId, dto);

        assertNotNull(result);
        verify(userRepository).updateUserNames(testUserId, dto);
    }

    @Test
    void updateUserNames_UserNotFound_ThrowsException() {
        UpdateUserNamesDto dto = new UpdateUserNamesDto();
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userServices.updateUserNames(testUserId, dto));
    }

    @Test
    void updatePassword_Success() {
        UpdateUserPasswordDto dto = new UpdateUserPasswordDto();
        dto.setNewPassword("parolanoua1234");
        String encodedPassword = "encoded_password";

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(dto.getNewPassword())).thenReturn(encodedPassword);
        when(userRepository.updatePassword(testUserId, encodedPassword)).thenReturn(testUser);

        User result = userServices.updatePassword(testUserId, dto);

        assertNotNull(result);
        verify(passwordEncoder).encode(dto.getNewPassword());
        verify(userRepository).updatePassword(testUserId, encodedPassword);
    }

    @Test
    void updatePassword_UserNotFound_ThrowsException() {
        UpdateUserPasswordDto dto = new UpdateUserPasswordDto();
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userServices.updatePassword(testUserId, dto));
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.deleteUser(testUserId)).thenReturn(true);

        boolean result = userServices.deleteUser(testUserId);

        assertTrue(result);
        verify(userRepository).deleteUser(testUserId);
    }

    @Test
    void deleteUser_UserNotFound_ThrowsException() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userServices.deleteUser(testUserId));
    }

}