package com.example.spendwise.domain.repositories;

import com.example.spendwise.domain.entities.User;

import java.util.Optional;
import java.util.UUID;

public interface IUserRepository {
    Optional<User> findById(UUID id);
    boolean existsByEmail(String email);
    User createUser(User user);
    User updatePassword(UUID id, String newPassword);
}
