package com.example.spendwise.application.services;

import com.example.spendwise.application.dtos.tranzaction.CreateTranzactionDto;
import com.example.spendwise.domain.repositories.ITranzactionRepository;
import com.example.spendwise.application.factory.EntityFactory;
import com.example.spendwise.domain.entities.Tranzaction;
import org.springframework.stereotype.Service;

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
}
