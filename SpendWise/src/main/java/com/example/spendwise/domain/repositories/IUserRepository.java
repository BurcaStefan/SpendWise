package com.example.spendwise.domain.repositories;

import com.example.spendwise.application.dtos.user.UpdateUserNamesDto;
import com.example.spendwise.domain.entities.User;

import java.util.Optional;
import java.util.UUID;

public interface IUserRepository {
    Optional<User> findById(UUID id);
    boolean existsByEmail(String email);
    User createUser(User user);
    User updatePassword(UUID id, String newPassword);
    User updateUserNames(UUID id, UpdateUserNamesDto dto);
    boolean deleteUser(UUID id);
    User getUserById(UUID id);
    String loginUser(String email, String password);
}
