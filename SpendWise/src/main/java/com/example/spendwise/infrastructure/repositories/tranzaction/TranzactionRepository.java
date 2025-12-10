package com.example.spendwise.infrastructure.repositories.tranzaction;

import com.example.spendwise.application.dtos.tranzaction.UpdateTranzactionDto;
import com.example.spendwise.domain.repositories.ITranzactionRepository;
import com.example.spendwise.domain.entities.Tranzaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
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
        Tranzaction existingTranzaction = tranzactionRepository.findById(tranzactionId)
                .orElseThrow(() -> {
                    return new RuntimeException("Transaction not found");
                });


        existingTranzaction.setType(tranzactionDto.getTranzactionType());
        existingTranzaction.setCategory(tranzactionDto.getCategoryType());
        existingTranzaction.setValue(tranzactionDto.getValue());
        existingTranzaction.setDate(tranzactionDto.getDate());
        existingTranzaction.setRecurrent(tranzactionDto.isRecurrent());
        existingTranzaction.setDescription(tranzactionDto.getDescription());
        try {
            Tranzaction saved = tranzactionRepository.save(existingTranzaction);

            return saved;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public Tranzaction getTranzactionById(UUID tranzactionId) {
        return tranzactionRepository.findById(tranzactionId).orElse(null);
    }

    @Override
    public boolean deleteTranzaction(UUID tranzactionId) {
        if(!tranzactionRepository.existsById(tranzactionId)) {
            return false;
        }
        tranzactionRepository.deleteById(tranzactionId);
        return true;
    }

    @Override
    public Page<Tranzaction> getTranzactionsByAccountId(UUID accountId, Pageable pageable) {
        return tranzactionRepository.findByAccountId(accountId, pageable);
    }

    @Override
    public Page<Tranzaction> filterTranzactionsByAccount(UUID accountId, Specification<Tranzaction> specification, Pageable pageable) {
        Specification<Tranzaction> accountSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("accountId"), accountId);

        Specification<Tranzaction> combinedSpec;
        if (specification != null) {
            combinedSpec = accountSpec.and(specification);
        } else {
            combinedSpec = accountSpec;
        }

        return tranzactionRepository.findAll(combinedSpec, pageable);
    }

    @Override
    public List<Tranzaction> findAll(Specification<Tranzaction> spec) {
        return tranzactionRepository.findAll(spec);
    }
}
