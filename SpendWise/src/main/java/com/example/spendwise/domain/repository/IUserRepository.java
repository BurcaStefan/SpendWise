package com.example.spendwise.domain.repository;

import com.example.spendwise.domain.entities.User;

import java.util.Optional;
import java.util.UUID;

public interface IUserRepository {
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    User createUser(User user);
    boolean existsByEmail(String email);
}
