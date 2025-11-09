package com.example.spendwise.tranzaction;

import com.example.spendwise.controllers.TranzactionController;
import com.example.spendwise.application.services.TranzactionServices;
import com.example.spendwise.application.dtos.tranzaction.CreateTranzactionDto;
import com.example.spendwise.application.dtos.tranzaction.UpdateTranzactionDto;
import com.example.spendwise.domain.entities.Tranzaction;
import com.example.spendwise.domain.entities.TranzactionType;
import com.example.spendwise.domain.entities.CategoryType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TranzactionControllerTest {
    @Mock
    private TranzactionServices tranzactionServices;

    private TranzactionController controller;
    private UUID id;
    private Tranzaction testTranzaction;

    @BeforeEach
    void setUp() {
        controller = new TranzactionController(tranzactionServices);
        id = UUID.randomUUID();
        testTranzaction = new Tranzaction(id, UUID.randomUUID(), TranzactionType.VENIT, CategoryType.HAINE, 120.0, LocalDate.now(), false, "desc");
    }

    @Test
    void createTranzaction_Success() {
        CreateTranzactionDto dto = new CreateTranzactionDto(testTranzaction.getAccountId(), TranzactionType.VENIT, CategoryType.HAINE, 120.0, false, "desc");
        when(tranzactionServices.createtranzaction(dto)).thenReturn(testTranzaction);

        var response = controller.createTranzaction(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testTranzaction, response.getBody());
    }

    @Test
    void getTranzactionById_Success() {
        when(tranzactionServices.getTranzactionById(id)).thenReturn(testTranzaction);

        var response = controller.getTranzactionById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testTranzaction, response.getBody());
    }

    @Test
    void getTranzactionById_Fail_PropagatesException() {
        when(tranzactionServices.getTranzactionById(id)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> controller.getTranzactionById(id));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void updateTranzaction_Success() {
        UpdateTranzactionDto dto = new UpdateTranzactionDto(TranzactionType.CHELTUIALA, CategoryType.HAINE, LocalDate.now(), 50.0, true, "u");
        when(tranzactionServices.updateTranzaction(id, dto)).thenReturn(testTranzaction);

        var response = controller.updateTranzaction(id, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testTranzaction, response.getBody());
    }

    @Test
    void deleteTranzaction_Success() {
        when(tranzactionServices.deleteTranzaction(id)).thenReturn(true);

        var response = controller.deleteTranzaction(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void deleteTranzaction_NotFound() {
        when(tranzactionServices.deleteTranzaction(id)).thenReturn(false);

        var response = controller.deleteTranzaction(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}
