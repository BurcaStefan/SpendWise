package com.example.spendwise.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tranzaction")
public class Tranzaction {
    @Id
    @Column(name = "tranzaction_id", nullable = false)
    private UUID tranzactionId;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    private BudgetAccount account;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TranzactionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private CategoryType category;

    @Column(name = "value", nullable = false)
    private double value;

    @Column(name = "date", nullable = false)
    private LocalDate date = LocalDate.now();

    @Column(name = "recurrent", nullable = false)
    private boolean recurrent = false;

    @Column(name = "description")
    private String description;

    public Tranzaction() {}

    public Tranzaction(UUID tranzactionId, UUID accountId, TranzactionType type, CategoryType category,
                       double value, LocalDate date, boolean recurrent, String description) {
        this.tranzactionId = tranzactionId;
        this.accountId = accountId;
        this.type = type;
        this.category = category;
        this.value = value;
        this.date = date;
        this.recurrent = recurrent;
        this.description = description;
    }

    public UUID getTranzactionId() { return tranzactionId; }
    public void setTranzactionId(UUID tranzactionId) { this.tranzactionId = tranzactionId; }

    public UUID getAccountId() { return accountId; }
    public void setAccountId(UUID accountId) { this.accountId = accountId; }

    public BudgetAccount getAccount() { return account; }
    public void setAccount(BudgetAccount account) { this.account = account; /* if needed, also set accountId manually */ }

    public TranzactionType getType() { return type; }
    public void setType(TranzactionType type) { this.type = type; }

    public CategoryType getCategory() { return category; }
    public void setCategory(CategoryType category) { this.category = category; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public boolean isRecurrent() { return recurrent; }
    public void setRecurrent(boolean recurrent) { this.recurrent = recurrent; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
