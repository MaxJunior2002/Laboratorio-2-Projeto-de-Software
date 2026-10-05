package com.projetosft.labdois.modules.matricula.repository;

import com.projetosft.labdois.modules.matricula.domain.PeriodoInscricao;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PeriodoInscricaoRepository extends JpaRepository<PeriodoInscricao, UUID> {

    Optional<PeriodoInscricao> findByPeriodo(String periodo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PeriodoInscricao p where p.periodo = :periodo")
    Optional<PeriodoInscricao> findLockedByPeriodo(@Param("periodo") String periodo);
}
