package com.example.spendwise.application.dtos.tranzaction;
import com.example.spendwise.domain.entities.TranzactionType;
import com.example.spendwise.domain.entities.CategoryType;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public class CreateTranzactionDto {
    @NotBlank
    public UUID accountId;
    @NotBlank
    public TranzactionType tranzactionType;
    @NotBlank
    public CategoryType categoryType;
    @NotBlank
    public double value;
    @NotBlank
    public boolean recurrent;
    public String description;
}
