package com.projetosft.labdois.modules.matricula.repository;

import com.projetosft.labdois.modules.matricula.domain.MatriculaDisciplina;
import com.projetosft.labdois.modules.matricula.domain.StatusMatricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface MatriculaDisciplinaRepository extends JpaRepository<MatriculaDisciplina, UUID> {

    long countByDisciplina_IdAndMatricula_PeriodoAndMatricula_Status(
            UUID disciplinaId, String periodo, StatusMatricula status);

    @Query("""
            select count(distinct md.matricula.aluno.id)
            from MatriculaDisciplina md
            where md.disciplina.id = :disciplinaId
              and md.matricula.periodo = :periodo
              and md.matricula.status = :status
            """)
    long contarAlunosAtivosPorDisciplinaEPeriodo(
            @Param("disciplinaId") UUID disciplinaId,
            @Param("periodo") String periodo,
            @Param("status") StatusMatricula status);
}
