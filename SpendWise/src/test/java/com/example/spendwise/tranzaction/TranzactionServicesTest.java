package com.example.spendwise.tranzaction;

import com.example.spendwise.application.dtos.tranzaction.CreateTranzactionDto;
import com.example.spendwise.application.dtos.tranzaction.UpdateTranzactionDto;
import com.example.spendwise.application.services.TranzactionServices;
import com.example.spendwise.domain.entities.Tranzaction;
import com.example.spendwise.domain.entities.TranzactionType;
import com.example.spendwise.domain.entities.CategoryType;
import com.example.spendwise.domain.repositories.ITranzactionRepository;
import com.example.spendwise.domain.factory.EntityFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TranzactionServicesTest {
    @Mock
    private ITranzactionRepository tranzactionRepository;
    @Mock
    private EntityFactory<Tranzaction, CreateTranzactionDto> tranzactionFactory;

    private TranzactionServices tranzactionServices;
    private UUID id;
    private Tranzaction testTranzaction;

    @BeforeEach
    void setUp() {
        tranzactionServices = new TranzactionServices(tranzactionRepository, tranzactionFactory);
        id = UUID.randomUUID();
        testTranzaction = new Tranzaction(id, UUID.randomUUID(), TranzactionType.VENIT, CategoryType.HAINE, 100.0, LocalDate.now(), false, "desc");
    }

    @Test
    void createTranzaction_Success() {
        CreateTranzactionDto dto = new CreateTranzactionDto(testTranzaction.getAccountId(), TranzactionType.VENIT, CategoryType.HAINE, 100.0, false, "desc");
        when(tranzactionFactory.create(dto)).thenReturn(testTranzaction);
        when(tranzactionRepository.createTranzaction(testTranzaction)).thenReturn(testTranzaction);

        Tranzaction result = tranzactionServices.createtranzaction(dto);

        assertNotNull(result);
        assertEquals(testTranzaction.getTranzactionId(), result.getTranzactionId());
        verify(tranzactionFactory).create(dto);
        verify(tranzactionRepository).createTranzaction(testTranzaction);
    }

    @Test
    void updateTranzaction_Success() {
        UpdateTranzactionDto dto = new UpdateTranzactionDto(TranzactionType.CHELTUIALA, CategoryType.HAINE, LocalDate.now(), 50.0, true, "upd");
        when(tranzactionRepository.getTranzactionById(id)).thenReturn(testTranzaction);
        when(tranzactionRepository.updateTranzaction(id, dto)).thenReturn(testTranzaction);

        Tranzaction result = tranzactionServices.updateTranzaction(id, dto);

        assertNotNull(result);
        verify(tranzactionRepository).getTranzactionById(id);
        verify(tranzactionRepository).updateTranzaction(id, dto);
    }

    @Test
    void updateTranzaction_NotFound_Throws() {
        UpdateTranzactionDto dto = new UpdateTranzactionDto();
        when(tranzactionRepository.getTranzactionById(id)).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> tranzactionServices.updateTranzaction(id, dto));
    }

    @Test
    void getTranzactionById_Success() {
        when(tranzactionRepository.getTranzactionById(id)).thenReturn(testTranzaction);

        Tranzaction result = tranzactionServices.getTranzactionById(id);

        assertNotNull(result);
        assertEquals(id, result.getTranzactionId());
    }

    @Test
    void getTranzactionById_NotFound_Throws() {
        when(tranzactionRepository.getTranzactionById(id)).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> tranzactionServices.getTranzactionById(id));
    }

    @Test
    void deleteTranzaction_Success() {
        when(tranzactionRepository.getTranzactionById(id)).thenReturn(testTranzaction);
        when(tranzactionRepository.deleteTranzaction(id)).thenReturn(true);

        boolean result = tranzactionServices.deleteTranzaction(id);

        assertTrue(result);
        verify(tranzactionRepository).deleteTranzaction(id);
    }

    @Test
    void deleteTranzaction_NotFound_Throws() {
        when(tranzactionRepository.getTranzactionById(id)).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> tranzactionServices.deleteTranzaction(id));
    }
}
