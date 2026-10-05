package com.projetosft.labdois.modules.disciplina.repository;

import com.projetosft.labdois.modules.disciplina.domain.Disciplina;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface DisciplinaRepository extends JpaRepository<Disciplina, UUID> {

    boolean existsByCurso_Id(UUID cursoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from Disciplina d where d.id = :id")
    Optional<Disciplina> findLockedById(@Param("id") UUID id);
}
