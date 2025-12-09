package com.example.spendwise.application.dtos.statistics;

import java.util.Map;

public class IncomeStatisticsDto {
    private Integer year;
    private Map<Integer, Double> incomeByMonth;

    public IncomeStatisticsDto(Integer year, Map<Integer, Double> incomeByMonth) {
        this.year = year;
        this.incomeByMonth = incomeByMonth;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Map<Integer, Double> getIncomeByMonth() {
        return incomeByMonth;
    }

    public void setIncomeByMonth(Map<Integer, Double> incomeByMonth) {
        this.incomeByMonth = incomeByMonth;
    }
}
