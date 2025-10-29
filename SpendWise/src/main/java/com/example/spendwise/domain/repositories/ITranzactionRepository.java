package com.example.spendwise.domain.repositories;

import com.example.spendwise.application.dtos.tranzaction.UpdateTranzactionDto;
import com.example.spendwise.domain.entities.Tranzaction;

import java.util.UUID;

public interface ITranzactionRepository {
    Tranzaction createTranzaction(Tranzaction tranzaction);
    Tranzaction updateTranzaction(UUID tranzactionId, UpdateTranzactionDto tranzactionDto);
}
