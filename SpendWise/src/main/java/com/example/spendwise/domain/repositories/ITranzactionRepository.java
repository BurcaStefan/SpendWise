package com.example.spendwise.domain.repositories;

import com.example.spendwise.domain.entities.Tranzaction;

public interface ITranzactionRepository {
    Tranzaction createTranzaction(Tranzaction tranzaction);
}
