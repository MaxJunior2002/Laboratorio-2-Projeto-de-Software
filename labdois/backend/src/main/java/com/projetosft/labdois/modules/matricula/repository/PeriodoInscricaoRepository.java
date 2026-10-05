package com.projetosft.labdois.modules.matricula.repository;

import com.projetosft.labdois.modules.matricula.domain.PeriodoInscricao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PeriodoInscricaoRepository extends JpaRepository<PeriodoInscricao, UUID> {

    Optional<PeriodoInscricao> findByPeriodo(String periodo);
}
