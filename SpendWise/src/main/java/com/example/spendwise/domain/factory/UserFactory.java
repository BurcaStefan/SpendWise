package com.example.spendwise.domain.factory;

import com.example.spendwise.application.dtos.user.CreateUserDto;
import com.example.spendwise.domain.entities.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("userFactory")
public class UserFactory implements EntityFactory<User, CreateUserDto> {

    private final PasswordEncoder passwordEncoder;

    public UserFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User create(CreateUserDto dto) {
        User newUser = new User();
        newUser.setId(UUID.randomUUID());
        newUser.setFirstname(dto.getFirstname());
        newUser.setLastname(dto.getLastname());
        newUser.setEmail(dto.getEmail());
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        return newUser;
    }
}
