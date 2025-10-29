package com.example.spendwise.application.services;

import com.example.spendwise.application.dtos.tranzaction.CreateTranzactionDto;
import com.example.spendwise.application.dtos.tranzaction.UpdateTranzactionDto;
import com.example.spendwise.domain.repositories.ITranzactionRepository;
import com.example.spendwise.application.factory.EntityFactory;
import com.example.spendwise.domain.entities.Tranzaction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class TranzactionServices {
    private final ITranzactionRepository tranzactionRepository;
    private final EntityFactory <Tranzaction, CreateTranzactionDto> tranzactionFactory;
    public TranzactionServices(ITranzactionRepository tranzactionRepository,
                              EntityFactory<Tranzaction, CreateTranzactionDto> tranzactionFactory) {
        this.tranzactionRepository = tranzactionRepository;
        this.tranzactionFactory = tranzactionFactory;
    }

    public Tranzaction createtranzaction(CreateTranzactionDto dto) {
        Tranzaction tranzaction = tranzactionFactory.create(dto);
        return tranzactionRepository.createTranzaction(tranzaction);
    }

//    public Tranzaction updateTranzaction(UUID tranzactionId, UpdateTranzactionDto dto) {
//        Tranzaction tranzacaton = tranzactionRepository.findById(tranzactionId).orElseThrow(() ->
//                new ResponseStatusException(HttpStatus.NOT_FOUND, "Tranzaction not found"));
//
//        tranzacaton.setType(dto.getTranzactionType());
//        tranzacaton.setCategory(dto.getCategoryType());
//        tranzacaton.setValue(dto.getValue());
//        tranzacaton.setDate(dto.getDate());
//        tranzacaton.setRecurrent(dto.isRecurrent());
//        tranzacaton.setDescription(dto.getDescription());
//        return tranzactionRepository.updateTranzaction(tranzactionId, dto);
//    }

    public Tranzaction getTranzactionById(UUID tranzactionId) {
        Tranzaction tranzaction = tranzactionRepository.getTranzactionById(tranzactionId);
        if (tranzaction == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tranzaction not found");
        }
        return tranzaction;
    }
}
