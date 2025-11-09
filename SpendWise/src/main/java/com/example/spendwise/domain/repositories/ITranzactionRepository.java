package com.example.spendwise.domain.repositories;

import com.example.spendwise.application.dtos.tranzaction.UpdateTranzactionDto;
import com.example.spendwise.domain.entities.Tranzaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ITranzactionRepository {
    Tranzaction createTranzaction(Tranzaction tranzaction);
    Tranzaction updateTranzaction(UUID tranzactionId, UpdateTranzactionDto tranzactionDto);
    Tranzaction getTranzactionById(UUID tranzactionId);
    boolean deleteTranzaction(UUID tranzactionId);
    Page<Tranzaction> getTranzactionsByAccountId(UUID accountId, Pageable pageable);
}
