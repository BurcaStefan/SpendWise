package com.example.spendwise.application.services;

import com.example.spendwise.application.dtos.tranzaction.CreateTranzactionDto;
import com.example.spendwise.application.dtos.tranzaction.TranzactionFilterDto;
import com.example.spendwise.application.dtos.tranzaction.UpdateTranzactionDto;
import com.example.spendwise.domain.repositories.ITranzactionRepository;
import com.example.spendwise.domain.factory.EntityFactory;
import com.example.spendwise.domain.entities.Tranzaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class TranzactionServices {
    private final ITranzactionRepository tranzactionRepository;
    private final EntityFactory <Tranzaction, CreateTranzactionDto> tranzactionFactory;
    private final TranzactionQueryBuilder queryBuilder;

    public TranzactionServices(ITranzactionRepository tranzactionRepository,
                               EntityFactory<Tranzaction, CreateTranzactionDto> tranzactionFactory,
                               TranzactionQueryBuilder queryBuilder) {
        this.tranzactionRepository = tranzactionRepository;
        this.tranzactionFactory = tranzactionFactory;
        this.queryBuilder = queryBuilder;
    }

    public Tranzaction createtranzaction(CreateTranzactionDto dto) {
        Tranzaction tranzaction = tranzactionFactory.create(dto);
        return tranzactionRepository.createTranzaction(tranzaction);
    }

    public Tranzaction updateTranzaction(UUID tranzactionId, UpdateTranzactionDto dto) {
        Tranzaction tranzacaton = tranzactionRepository.getTranzactionById(tranzactionId);
        if (tranzacaton == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tranzaction not found");
        }

        tranzacaton.setType(dto.getTranzactionType());
        tranzacaton.setCategory(dto.getCategoryType());
        tranzacaton.setValue(dto.getValue());
        tranzacaton.setDate(dto.getDate());
        tranzacaton.setRecurrent(dto.isRecurrent());
        tranzacaton.setDescription(dto.getDescription());
        return tranzactionRepository.updateTranzaction(tranzactionId, dto);
    }

    public Tranzaction getTranzactionById(UUID tranzactionId) {
        Tranzaction tranzaction = tranzactionRepository.getTranzactionById(tranzactionId);
        if (tranzaction == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tranzaction not found");
        }
        return tranzaction;
    }

    public boolean deleteTranzaction(UUID tranzactionId) {
        if (tranzactionRepository.getTranzactionById(tranzactionId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tranzaction not found");
        }
        return tranzactionRepository.deleteTranzaction(tranzactionId);
    }

    public Page<Tranzaction> getTranzactionsByAccountId(UUID accountId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        return tranzactionRepository.getTranzactionsByAccountId(accountId, pageable);
    }

    public Page<Tranzaction> filterTranzactionsByAccount(UUID accountId, TranzactionFilterDto filterDto, int page, int size) {
        Specification<Tranzaction> specification = queryBuilder.buildSpecification(filterDto);
        Sort sort = queryBuilder.buildSort(filterDto);
        Pageable pageable = PageRequest.of(page, size, sort);
        return tranzactionRepository.filterTranzactionsByAccount(accountId, specification, pageable);
    }
}
