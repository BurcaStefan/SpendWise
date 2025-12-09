package com.example.spendwise.application.dtos.statistics;

public class CategoryStatDto {
    private String name;
    private double amount;
    private String color;

    public CategoryStatDto() {}

    public CategoryStatDto(String name, double amount, String color) {
        this.name = name;
        this.amount = amount;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public String getColor() {
        return color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
