package com.example.spendwise.domain.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "budget_account")
public class BudgetAccount {
    @Id
    @Column(name = "budget_account_id", nullable = false)
    private UUID budgetAccountId;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "value", nullable = false)
    private double value = 0.0;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tranzaction> tranzactions = new ArrayList<>();

    public BudgetAccount() {}

    public BudgetAccount(UUID budgetAccountId, UUID userId, double value) {
        this.budgetAccountId = budgetAccountId;
        this.userId = userId;
        this.value = value;
    }

    public UUID getBudgetAccountId() { return budgetAccountId; }
    public void setBudgetAccountId(UUID budgetAccountId) { this.budgetAccountId = budgetAccountId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; /* if needed, also set userId = user.getUserId(); */ }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }

    public List<Tranzaction> getTranzactions() { return tranzactions; }
    public void setTranzactions(List<Tranzaction> tranzactions) { this.tranzactions = tranzactions; }
}
