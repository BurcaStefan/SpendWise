package com.example.spendwise.user;

import com.example.spendwise.application.controllers.UserController;
import com.example.spendwise.application.dtos.user.CreateUserDto;
import com.example.spendwise.application.dtos.user.UpdateUserNamesDto;
import com.example.spendwise.application.dtos.user.UpdateUserPasswordDto;
import com.example.spendwise.application.services.UserServices;
import com.example.spendwise.domain.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserServices userServices;

    private UserController userController;
    private UUID testUserId;
    private User testUser;

    @BeforeEach
    void setUp() {
        userController = new UserController(userServices);
        testUserId = UUID.randomUUID();
        testUser = new User(testUserId, "Ion", "Popescu", "popescu@gmail.com", "parola1234");
    }

    @Test
    void createUser_Success() {
        CreateUserDto dto = new CreateUserDto();
        when(userServices.createUser(dto)).thenReturn(testUser);

        var response = userController.createUser(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testUser, response.getBody());
    }

    @Test
    void getUserById_Success() {
        when(userServices.getUserById(testUserId)).thenReturn(testUser);

        var response = userController.getUserById(testUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser, response.getBody());
    }

    @Test
    void getUserById_Fail_NotFoundThrows() {
        when(userServices.getUserById(testUserId)).thenThrow(new RuntimeException("User not found"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userController.getUserById(testUserId));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void updateUserPassword_Success() {
        UpdateUserPasswordDto dto = new UpdateUserPasswordDto();
        when(userServices.updatePassword(testUserId, dto)).thenReturn(testUser);

        var response = userController.updateUserPassword(testUserId, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser, response.getBody());
    }

    @Test
    void updateUserPassword_Fail_InvalidPassword() {
        UpdateUserPasswordDto dto = new UpdateUserPasswordDto();
        when(userServices.updatePassword(testUserId, dto)).thenThrow(new IllegalArgumentException("Invalid password"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userController.updateUserPassword(testUserId, dto));
        assertEquals("Invalid password", ex.getMessage());
    }

    @Test
    void updateUserNames_Success() {
        UpdateUserNamesDto dto = new UpdateUserNamesDto();
        User updated = new User(testUserId, "Ionel", "Ionescu", testUser.getEmail(), testUser.getPassword());
        when(userServices.updateUserNames(testUserId, dto)).thenReturn(updated);

        var response = userController.updateUserNames(testUserId, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updated, response.getBody());
    }

    @Test
    void updateUserNames_Fail_Throws() {
        UpdateUserNamesDto dto = new UpdateUserNamesDto();
        when(userServices.updateUserNames(testUserId, dto)).thenThrow(new RuntimeException("Update names failed"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userController.updateUserNames(testUserId, dto));
        assertEquals("Update names failed", ex.getMessage());
    }

    @Test
    void deleteUser_Success() {
        when(userServices.deleteUser(testUserId)).thenReturn(true);

        var response = userController.deleteUser(testUserId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertTrue(response.getBody());
    }

    @Test
    void deleteUser_Fail_NotFound() {
        when(userServices.deleteUser(testUserId)).thenReturn(false);

        var response = userController.deleteUser(testUserId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody());
    }
}
