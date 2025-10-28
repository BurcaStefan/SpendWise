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

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private BudgetAccount account;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private CategoryType category;

    @Column(name = "value", nullable = false)
    private double value;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "recurrent", nullable = false)
    private boolean recurrent = false;

    @Column(name = "description")
    private String description;

    public Tranzaction() {}

    public Tranzaction(UUID tranzactionId, BudgetAccount account, TransactionType type, CategoryType category,
                       double value, LocalDate date, boolean recurrent, String description) {
        this.tranzactionId = tranzactionId;
        this.account = account;
        this.type = type;
        this.category = category;
        this.value = value;
        this.date = date;
        this.recurrent = recurrent;
        this.description = description;
    }

    public UUID getTranzactionId() { return tranzactionId; }
    public void setTranzactionId(UUID tranzactionId) { this.tranzactionId = tranzactionId; }

    public BudgetAccount getAccount() { return account; }
    public void setAccount(BudgetAccount account) { this.account = account; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

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
