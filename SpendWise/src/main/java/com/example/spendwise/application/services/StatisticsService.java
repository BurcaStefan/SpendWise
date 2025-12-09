package com.example.spendwise.application.services;

import com.example.spendwise.application.dtos.statistics.CategoryStatDto;
import com.example.spendwise.application.dtos.statistics.StatisticsResponseDto;
import com.example.spendwise.domain.entities.CategoryType;
import com.example.spendwise.domain.entities.Tranzaction;
import com.example.spendwise.domain.entities.TranzactionType;
import com.example.spendwise.domain.repositories.ITranzactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {
    private final ITranzactionRepository tranzactionRepository;

    public StatisticsService(ITranzactionRepository tranzactionRepository) {
        this.tranzactionRepository = tranzactionRepository;
    }

    public StatisticsResponseDto getMonthlyExpensesByCategory(UUID accountId, int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        Specification<Tranzaction> spec = (root, query, cb) -> cb.and(
                cb.equal(root.get("type"), TranzactionType.EXPENSE),
                cb.between(root.get("date"), start, end)
        );


        Page<Tranzaction> page = tranzactionRepository.filterTranzactionsByAccount(accountId, spec, Pageable.unpaged());
        List<Tranzaction> tranzactions = page.getContent();

        Map<CategoryType, Double> sumsByCategory = tranzactions.stream()
                .collect(Collectors.groupingBy(
                        Tranzaction::getCategory,
                        Collectors.summingDouble(Tranzaction::getValue)
                ));

        Map<CategoryType, String> displayNames = Map.of(
                CategoryType.FOOD, "Food",
                CategoryType.CLOTHING, "Clothing",
                CategoryType.TRANSPORT, "Transport",
                CategoryType.BILLS, "Bills",
                CategoryType.ENTERTAINMENT, "Entertainment",
                CategoryType.HEALTH, "Health",
                CategoryType.EDUCATION, "Education",
                CategoryType.TRAVEL, "Travel",
                CategoryType.GIFTS, "Gifts",
                CategoryType.OTHER, "Other"
        );

        Map<CategoryType, String> colors = Map.of(
                CategoryType.FOOD, "#FF6384",
                CategoryType.TRANSPORT, "#36A2EB",
                CategoryType.ENTERTAINMENT, "#FFCE56",
                CategoryType.BILLS, "#4BC0C0",
                CategoryType.CLOTHING, "#9966FF",
                CategoryType.HEALTH, "#FF9F40",
                CategoryType.EDUCATION, "#8A2BE2",
                CategoryType.TRAVEL, "#00A36C",
                CategoryType.GIFTS, "#C71585",
                CategoryType.OTHER, "#A9A9A9"
        );

        List<CategoryStatDto> categories = sumsByCategory.entrySet().stream()
                .map(e -> new CategoryStatDto(
                        displayNames.getOrDefault(e.getKey(), e.getKey().name()),
                        Math.round(e.getValue() * 100.0) / 100.0,
                        colors.getOrDefault(e.getKey(), "#A9A9A9")
                ))
                .sorted(Comparator.comparingDouble(CategoryStatDto::getAmount).reversed())
                .collect(Collectors.toList());

        double total = categories.stream().mapToDouble(CategoryStatDto::getAmount).sum();

        String period = Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + year;

        return new StatisticsResponseDto(categories, Math.round(total * 100.0) / 100.0, period);
    }
}
