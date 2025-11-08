package com.example.spendwise.controllers;

import com.example.spendwise.application.dtos.user.CreateUserDto;
import com.example.spendwise.application.dtos.user.UpdateUserNamesDto;
import com.example.spendwise.application.dtos.user.UpdateUserPasswordDto;
import com.example.spendwise.application.services.UserServices;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.spendwise.domain.entities.User;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserServices userServices;

    public UserController(UserServices userServices) {
        this.userServices = userServices;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserDto createUserDto)
    {
        User createdUser = userServices.createUser(createUserDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable UUID userId)
    {
        User user = userServices.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{userId}/password")
    public ResponseEntity<User> updateUserPassword(@PathVariable UUID userId, @Valid @RequestBody UpdateUserPasswordDto dto)
    {
        User updatedUser = userServices.updatePassword(userId, dto);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{userId}/names")
    public ResponseEntity<User> updateUserNames(@PathVariable UUID userId, @Valid @RequestBody UpdateUserNamesDto dto)
    {
        User updatedUser = userServices.updateUserNames(userId, dto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable UUID userId)
    {
        boolean deleted = userServices.deleteUser(userId);
        if (deleted) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(true);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }
    }
}
