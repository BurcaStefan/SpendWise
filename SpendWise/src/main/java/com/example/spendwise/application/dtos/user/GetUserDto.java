package com.example.spendwise.application.dtos.user;

import java.util.UUID;

public class GetUserDto {

    private UUID id;
    private String firstname;
    private String lastname;
    private String email;

    public GetUserDto(UUID id, String firstname, String lastname, String email) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    public UUID getId() { return id; }
    public String getFirstname() { return firstname; }
    public String getLastname() { return lastname; }
    public String getEmail() { return email; }
}
