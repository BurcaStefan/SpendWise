package com.example.spendwise.application.factory;
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
        tranzaction.setAccountId(dto.accountId);
        tranzaction.setType(dto.tranzactionType);
        tranzaction.setCategory(dto.categoryType);
        tranzaction.setValue(dto.value);
        tranzaction.setRecurrent(dto.recurrent);
        tranzaction.setDescription(dto.description);
        return tranzaction;
    }

}
