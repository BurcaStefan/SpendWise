package com.example.spendwise.infrastructure.repositories.user;

import com.example.spendwise.application.dtos.user.UpdateUserNamesDto;
import com.example.spendwise.domain.entities.User;
import com.example.spendwise.domain.repositories.IUserRepository;
import com.example.spendwise.infrastructure.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl implements IUserRepository {

    private final SpringDataUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserRepositoryImpl(SpringDataUserRepository userRepository,
                              PasswordEncoder passwordEncoder,
                              JwtUtil jwtUtil)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
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
    public User getUserById(UUID id) {
        return userRepository.findById(id).orElse(null);
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

    @Override
    public boolean deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }

    @Override
    public String loginUser(String email, String rawPassword) {
        Optional<User> maybeUser = userRepository.findByEmail(email);
        if (maybeUser.isEmpty()) {
            return null;
        }
        User user = maybeUser.get();
        String storedHash = user.getPassword();
        if (storedHash != null && passwordEncoder.matches(rawPassword, storedHash)) {
            return jwtUtil.generateToken(user.getId().toString());
        }
        return null;
    }
}
