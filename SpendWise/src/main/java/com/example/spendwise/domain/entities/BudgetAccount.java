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

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "sold", nullable = false)
    private double sold = 0.0;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tranzaction> tranzactions = new ArrayList<>();

    public BudgetAccount() {}

    public BudgetAccount(UUID budgetAccountId, User user, double sold) {
        this.budgetAccountId = budgetAccountId;
        this.user = user;
        this.sold = sold;
    }

    public UUID getBudgetAccountId() { return budgetAccountId; }
    public void setBudgetAccountId(UUID budgetAccountId) { this.budgetAccountId = budgetAccountId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public double getSold() { return sold; }
    public void setSold(double sold) { this.sold = sold; }

    public List<Tranzaction> getTranzactions() { return tranzactions; }
    public void setTranzactions(List<Tranzaction> tranzactions) { this.tranzactions = tranzactions; }
}
