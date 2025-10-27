package com.example.spendwise.application.controllers;

import com.example.spendwise.application.dtos.user.CreateUserDto;
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

    @PatchMapping("/{userId}/password")
        public ResponseEntity<User> updateUserPassword(@PathVariable UUID userId, @Valid @RequestBody UpdateUserPasswordDto dto)
        {
            User updatedUser = userServices.updatePassword(userId, dto);
            return ResponseEntity.ok(updatedUser);
        }
}
