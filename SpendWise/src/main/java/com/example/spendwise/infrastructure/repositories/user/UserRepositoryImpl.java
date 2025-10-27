package com.example.spendwise.infrastructure.repositories.user;

import com.example.spendwise.application.dtos.user.UpdateUserNamesDto;
import com.example.spendwise.domain.entities.User;
import com.example.spendwise.domain.repositories.IUserRepository;
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
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updatePassword(UUID id, String newPassword) {
        Optional<User> maybeUser = userRepository.findById(id);
        if (maybeUser.isEmpty()) {
            return null;
        }
        User user = maybeUser.get();
        user.setPassword(newPassword);
        return userRepository.save(user);
    }

    @Override
    public User updateUserNames(UUID id, UpdateUserNamesDto dto)
    {
        Optional<User> maybeUser = userRepository.findById(id);
        if (maybeUser.isEmpty()) {
            return null;
        }
        User user = maybeUser.get();
        user.setFirstname(dto.getFirstname());
        user.setLastname(dto.getLastname());
        return userRepository.save(user);
    }
}
