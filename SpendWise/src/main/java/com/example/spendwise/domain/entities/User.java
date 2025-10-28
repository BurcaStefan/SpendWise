package com.example.spendwise.domain.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name="user_id", nullable=false)
    private UUID userId;

    @Column(name = "firstname", nullable = false)
    private String firstname;

    @Column(name = "lastname", nullable = false)
    private String lastname;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String passwordHash;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private BudgetAccount budgetAccount;

    public User(){}

    public User(UUID userId, String firstname, String lastname, String email, String passwordHash) {
        this.userId = userId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public UUID getId() { return userId; }
    public void setId(UUID id) { this.userId = id; }

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return passwordHash; }
    public void setPassword(String passwordHash) { this.passwordHash = passwordHash; }

    public BudgetAccount getBudgetAccount() { return budgetAccount; }
    public void setBudgetAccount(BudgetAccount budgetAccount) { this.budgetAccount = budgetAccount; }
}
