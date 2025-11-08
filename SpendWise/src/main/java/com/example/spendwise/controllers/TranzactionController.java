package com.example.spendwise.controllers;

import com.example.spendwise.application.dtos.tranzaction.CreateTranzactionDto;
import com.example.spendwise.application.dtos.tranzaction.UpdateTranzactionDto;
import com.example.spendwise.domain.entities.Tranzaction;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.spendwise.application.services.TranzactionServices;
import java.util.UUID;

@RestController
@RequestMapping("/api/tranzactions")
public class TranzactionController {
    private final TranzactionServices tranzactionServices;
    public TranzactionController(TranzactionServices tranzactionServices) {
        this.tranzactionServices = tranzactionServices;
    }

    @PostMapping
    public ResponseEntity<Tranzaction> createTranzaction(@Valid @RequestBody CreateTranzactionDto createTranzactionDto) {
        Tranzaction tranzaction = tranzactionServices.createtranzaction(createTranzactionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(tranzaction);
    }

    @GetMapping("/{tranzactionId}")
    public ResponseEntity<Tranzaction> getTranzactionById(@PathVariable UUID tranzactionId) {
        Tranzaction tranzaction = tranzactionServices.getTranzactionById(tranzactionId);
        return ResponseEntity.ok(tranzaction);
    }

    @PutMapping("/{tranzactionId}")
    public ResponseEntity<Tranzaction> updateTranzaction(@PathVariable UUID tranzactionId, @Valid @RequestBody UpdateTranzactionDto updateTranzactionDto) {
        Tranzaction updatedTranzaction = tranzactionServices.updateTranzaction(tranzactionId, updateTranzactionDto);
        return ResponseEntity.ok(updatedTranzaction);
    }

    @DeleteMapping("/{tranzactionId}")
    public ResponseEntity<Void> deleteTranzaction(@PathVariable UUID tranzactionId) {
        boolean deleted = tranzactionServices.deleteTranzaction(tranzactionId);
        if (deleted) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
