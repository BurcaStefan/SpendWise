package com.example.spendwise.infrastructure.user;

import com.example.spendwise.domain.entities.User;
import com.example.spendwise.domain.repository.IUserRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl implements IUserRepository {

    private final SpringDataUserRepository userRepository;

    public UserRepositoryImpl(SpringDataUserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }
}
