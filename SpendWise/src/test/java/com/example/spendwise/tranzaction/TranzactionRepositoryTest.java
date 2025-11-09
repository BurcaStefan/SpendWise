package com.example.spendwise.tranzaction;

import com.example.spendwise.application.dtos.tranzaction.UpdateTranzactionDto;
import com.example.spendwise.domain.entities.Tranzaction;
import com.example.spendwise.domain.entities.TranzactionType;
import com.example.spendwise.domain.entities.CategoryType;
import com.example.spendwise.infrastructure.repositories.tranzaction.SpringDataTranzactionRepository;
import com.example.spendwise.infrastructure.repositories.tranzaction.TranzactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TranzactionRepositoryTest {
    @Mock
    private SpringDataTranzactionRepository springRepo;

    private TranzactionRepository repository;
    private UUID id;
    private Tranzaction testTranzaction;

    @BeforeEach
    void setUp() {
        repository = new TranzactionRepository(springRepo);
        id = UUID.randomUUID();
        testTranzaction = new Tranzaction(id, UUID.randomUUID(), TranzactionType.VENIT, CategoryType.HAINE, 200.0, LocalDate.now(), false, "desc");
    }

    @Test
    void createTranzaction_Success() {
        when(springRepo.save(any(Tranzaction.class))).thenReturn(testTranzaction);

        Tranzaction result = repository.createTranzaction(testTranzaction);

        assertNotNull(result);
        assertEquals(testTranzaction.getTranzactionId(), result.getTranzactionId());
        verify(springRepo).save(testTranzaction);
    }

    @Test
    void updateTranzaction_Success() {
        UpdateTranzactionDto dto = new UpdateTranzactionDto(TranzactionType.CHELTUIALA, CategoryType.HAINE, LocalDate.now(), 80.0, true, "u");
        when(springRepo.findById(id)).thenReturn(Optional.of(testTranzaction));
        when(springRepo.save(any(Tranzaction.class))).thenReturn(testTranzaction);

        Tranzaction result = repository.updateTranzaction(id, dto);

        assertNotNull(result);
        verify(springRepo).findById(id);
        verify(springRepo).save(any(Tranzaction.class));
    }

    @Test
    void updateTranzaction_NotFound_ReturnsNull() {
        UpdateTranzactionDto dto = new UpdateTranzactionDto();
        when(springRepo.findById(id)).thenReturn(Optional.empty());

        Tranzaction result = repository.updateTranzaction(id, dto);

        assertNull(result);
        verify(springRepo, never()).save(any());
    }

    @Test
    void getTranzactionById_Success() {
        when(springRepo.findById(id)).thenReturn(Optional.of(testTranzaction));

        Tranzaction result = repository.getTranzactionById(id);

        assertNotNull(result);
        assertEquals(id, result.getTranzactionId());
    }

    @Test
    void getTranzactionById_NotFound_ReturnsNull() {
        when(springRepo.findById(id)).thenReturn(Optional.empty());

        Tranzaction result = repository.getTranzactionById(id);

        assertNull(result);
    }

    @Test
    void deleteTranzaction_Success() {
        when(springRepo.existsById(id)).thenReturn(true);
        doNothing().when(springRepo).deleteById(id);

        boolean result = repository.deleteTranzaction(id);

        assertTrue(result);
        verify(springRepo).deleteById(id);
    }

    @Test
    void deleteTranzaction_NotFound_ReturnsFalse() {
        when(springRepo.existsById(id)).thenReturn(false);

        boolean result = repository.deleteTranzaction(id);

        assertFalse(result);
        verify(springRepo, never()).deleteById(any());
    }
}
