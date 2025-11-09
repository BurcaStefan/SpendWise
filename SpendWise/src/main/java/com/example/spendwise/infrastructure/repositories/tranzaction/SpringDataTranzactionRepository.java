package com.example.spendwise.infrastructure.repositories.tranzaction;

import com.example.spendwise.domain.entities.Tranzaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataTranzactionRepository extends JpaRepository<Tranzaction, UUID>, JpaSpecificationExecutor<Tranzaction> {
    Optional<Tranzaction> findById(UUID id);
    Page<Tranzaction> findByAccountId(UUID accountId, Pageable pageable);
}
