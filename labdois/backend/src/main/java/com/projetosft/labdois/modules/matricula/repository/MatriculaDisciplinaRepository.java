package com.projetosft.labdois.modules.matricula.repository;

import com.projetosft.labdois.modules.matricula.domain.MatriculaDisciplina;
import com.projetosft.labdois.modules.matricula.domain.StatusMatricula;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MatriculaDisciplinaRepository extends JpaRepository<MatriculaDisciplina, UUID> {

    long countByDisciplina_IdAndMatricula_PeriodoAndMatricula_Status(
            UUID disciplinaId, String periodo, StatusMatricula status);
}
