package com.example.spendwise.application.dtos.statistics;

import java.util.Map;

public class AnnualIncomeStatisticsDto {
    private int year;
    private Map<Integer, Double> incomeByMonth;

    public AnnualIncomeStatisticsDto(int year, Map<Integer, Double> incomeByMonth) {
        this.year = year;
        this.incomeByMonth = incomeByMonth;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Map<Integer, Double> getIncomeByMonth() {
        return incomeByMonth;
    }

    public void setIncomeByMonth(Map<Integer, Double> incomeByMonth) {
        this.incomeByMonth = incomeByMonth;
    }
}
