package com.projetosft.labdois.modules.matricula.repository;

import com.projetosft.labdois.modules.matricula.domain.MatriculaDisciplina;
import com.projetosft.labdois.modules.matricula.domain.StatusMatricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;
import java.util.List;

public interface MatriculaDisciplinaRepository extends JpaRepository<MatriculaDisciplina, UUID> {

    long countByOferta_IdAndMatricula_Status(UUID ofertaId, StatusMatricula status);

    List<MatriculaDisciplina> findByOferta_Professor_IdAndOferta_PeriodoInscricao_PeriodoAndMatricula_Status(
            UUID professorId, String periodo, StatusMatricula status);

    @Query("""
            select count(distinct md.matricula.aluno.id)
            from MatriculaDisciplina md
            where md.oferta.id = :ofertaId
              and md.matricula.status = :status
            """)
    long contarAlunosAtivosPorOferta(
            @Param("ofertaId") UUID ofertaId,
            @Param("status") StatusMatricula status);
}
