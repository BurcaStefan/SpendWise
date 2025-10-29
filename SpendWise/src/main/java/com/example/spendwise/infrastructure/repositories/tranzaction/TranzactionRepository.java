package com.example.spendwise.infrastructure.repositories.tranzaction;

import com.example.spendwise.domain.repositories.ITranzactionRepository;
import com.example.spendwise.domain.entities.Tranzaction;
import org.springframework.stereotype.Repository;

@Repository
public class TranzactionRepository implements ITranzactionRepository {
    private final SpringDataTranzactionRepository tranzactionRepository;
    public TranzactionRepository(SpringDataTranzactionRepository tranzactionRepository) {
        this.tranzactionRepository = tranzactionRepository;
    }

    @Override
    public Tranzaction createTranzaction(Tranzaction tranzaction) {
        return tranzactionRepository.save(tranzaction);
    }

}
