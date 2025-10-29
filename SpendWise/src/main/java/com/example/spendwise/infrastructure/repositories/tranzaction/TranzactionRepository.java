package com.example.spendwise.infrastructure.repositories.tranzaction;

import com.example.spendwise.application.dtos.tranzaction.UpdateTranzactionDto;
import com.example.spendwise.domain.repositories.ITranzactionRepository;
import com.example.spendwise.domain.entities.Tranzaction;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

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

    @Override
    public Tranzaction updateTranzaction(UUID tranzactionId, UpdateTranzactionDto tranzactionDto) {
        Optional<Tranzaction> maybeTranzaction = tranzactionRepository.findById(tranzactionId);
        if (maybeTranzaction.isEmpty()) {
            return null;
        }
        Tranzaction tranzaction = maybeTranzaction.get();
        tranzaction.setType(tranzactionDto.getTranzactionType());
        tranzaction.setCategory(tranzactionDto.getCategoryType());
        tranzaction.setValue(tranzactionDto.getValue());
        tranzaction.setDate(tranzactionDto.getDate());
        tranzaction.setRecurrent(tranzactionDto.isRecurrent());
        tranzaction.setDescription(tranzactionDto.getDescription());

        return tranzactionRepository.save(tranzaction);
    }
}
