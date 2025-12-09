package com.example.spendwise.application.dtos.statistics;

import java.util.List;

public class StatisticsResponseDto {
    private List<CategoryStatDto> categories;
    private double total;
    private String period;

    public StatisticsResponseDto() {}

    public StatisticsResponseDto(List<CategoryStatDto> categories, double total, String period) {
        this.categories = categories;
        this.total = total;
        this.period = period;
    }

    public List<CategoryStatDto> getCategories() {
        return categories;
    }

    public double getTotal() {
        return total;
    }

    public String getPeriod() {
        return period;
    }

    public void setCategories(List<CategoryStatDto> categories) {
        this.categories = categories;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
}
