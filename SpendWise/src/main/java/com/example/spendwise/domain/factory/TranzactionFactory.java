package com.example.spendwise.domain.factory;
import com.example.spendwise.application.dtos.tranzaction.CreateTranzactionDto;
import com.example.spendwise.domain.entities.Tranzaction;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("tranzactionFactory")
public class TranzactionFactory implements EntityFactory<Tranzaction, CreateTranzactionDto> {
    @Override
    public Tranzaction create(CreateTranzactionDto dto) {
        Tranzaction tranzaction = new Tranzaction();
        tranzaction.setTranzactionId(UUID.randomUUID());
        tranzaction.setAccountId(dto.getAccountId());
        tranzaction.setType(dto.getTranzactionType());
        tranzaction.setCategory(dto.getCategoryType());
        tranzaction.setValue(dto.getValue());
        tranzaction.setRecurrent(dto.isRecurrent());
        tranzaction.setDescription(dto.getDescription());
        return tranzaction;
    }

}
