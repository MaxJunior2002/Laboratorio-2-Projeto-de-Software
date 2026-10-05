package com.projetosft.labdois.modules.disciplina.repository;

import com.projetosft.labdois.modules.disciplina.domain.OfertaDisciplina;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OfertaDisciplinaRepository extends JpaRepository<OfertaDisciplina, UUID> {

    boolean existsByDisciplina_IdAndPeriodoInscricao_Periodo(UUID disciplinaId, String periodo);

    List<OfertaDisciplina> findByPeriodoInscricao_Periodo(String periodo);

    List<OfertaDisciplina> findByProfessor_IdAndPeriodoInscricao_Periodo(UUID professorId, String periodo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from OfertaDisciplina o where o.id = :id")
    Optional<OfertaDisciplina> findLockedById(@Param("id") UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from OfertaDisciplina o where o.periodoInscricao.periodo = :periodo order by o.id")
    List<OfertaDisciplina> findAllLockedByPeriodo(@Param("periodo") String periodo);
}
