package com.example.spendwise.application.controllers;

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
    public ResponseEntity<Tranzaction> create(@Valid @RequestBody CreateTranzactionDto createTranzactionDto) {
        Tranzaction tranzaction = tranzactionServices.createtranzaction(createTranzactionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(tranzaction);
    }

    @GetMapping("/{tranzactionId}")
    public ResponseEntity<Tranzaction> getById(@PathVariable UUID tranzactionId) {
        Tranzaction tranzaction = tranzactionServices.getTranzactionById(tranzactionId);
        return ResponseEntity.ok(tranzaction);
    }

    @PutMapping("/{tranzactionId}")
    public ResponseEntity<Tranzaction> update(@PathVariable UUID tranzactionId, @Valid @RequestBody UpdateTranzactionDto updateTranzactionDto) {
        Tranzaction updatedTranzaction = tranzactionServices.updateTranzaction(tranzactionId, updateTranzactionDto);
        return ResponseEntity.ok(updatedTranzaction);
    }

}
