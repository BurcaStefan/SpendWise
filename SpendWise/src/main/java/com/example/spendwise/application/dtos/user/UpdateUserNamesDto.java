package com.example.spendwise.application.dtos.user;

import jakarta.validation.constraints.NotBlank;

public class UpdateUserNamesDto {
    @NotBlank
    private String firstname;

    @NotBlank
    private String lastname;

    public UpdateUserNamesDto() {}

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
