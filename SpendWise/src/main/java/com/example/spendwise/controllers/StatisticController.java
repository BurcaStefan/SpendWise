package com.example.spendwise.controllers;

import com.example.spendwise.application.dtos.statistics.AnnualIncomeStatisticsDto;
import com.example.spendwise.application.dtos.statistics.IncomeStatisticsDto;
import com.example.spendwise.application.dtos.statistics.StatisticsResponseDto;
import com.example.spendwise.application.services.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/statistics")
public class StatisticController {
    private final StatisticsService statisticsService;

    public StatisticController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/expenses/{accountId}")
    public ResponseEntity<StatisticsResponseDto> getMonthlyExpenses(
            @PathVariable UUID accountId,
            @RequestParam int month,
            @RequestParam int year) {
        StatisticsResponseDto response = statisticsService.getMonthlyExpensesByCategory(accountId, month, year);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/income/{budgetAccountId}/{year}")
    public ResponseEntity<AnnualIncomeStatisticsDto> getAnnualIncomeStatistics(
            @PathVariable String budgetAccountId,
            @PathVariable Integer year) {
        UUID accountId = UUID.fromString(budgetAccountId);
        AnnualIncomeStatisticsDto statistics = statisticsService.getAnnualIncomeStatistics(accountId, year);
        return ResponseEntity.ok(statistics);
    }


}
