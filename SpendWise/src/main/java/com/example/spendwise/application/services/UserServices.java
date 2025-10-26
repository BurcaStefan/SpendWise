package com.example.spendwise.application.services;

import com.example.spendwise.application.dtos.user.CreateUserDto;
import com.example.spendwise.application.factory.EntityFactory;
import com.example.spendwise.domain.entities.User;
import com.example.spendwise.domain.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Service
public class UserServices {

    private final IUserRepository userRepository;
    private final EntityFactory<User, CreateUserDto> userFactory;

    public UserServices(IUserRepository userRepository,
                        @Qualifier("userFactory") EntityFactory<User, CreateUserDto> userFactory) {
        this.userRepository = userRepository;
        this.userFactory = userFactory;
    }

    public User createUser(CreateUserDto createUserDto) {
        if (userRepository.existsByEmail(createUserDto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This email is already used");
        }

        User newUser = userFactory.create(createUserDto);
        return userRepository.createUser(newUser);
    }
}
