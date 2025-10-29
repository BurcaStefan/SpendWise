package com.example.spendwise.infrastructure.repositories.tranzaction;

import com.example.spendwise.domain.entities.Tranzaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SpringDataTranzactionRepository extends JpaRepository<Tranzaction, UUID> {
}
