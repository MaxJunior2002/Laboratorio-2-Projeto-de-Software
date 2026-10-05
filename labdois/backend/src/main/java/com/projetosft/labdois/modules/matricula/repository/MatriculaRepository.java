package com.projetosft.labdois.modules.matricula.repository;

import com.projetosft.labdois.modules.matricula.domain.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MatriculaRepository extends JpaRepository<Matricula, UUID> {

    Optional<Matricula> findByAluno_IdAndPeriodo(UUID alunoId, String periodo);

    boolean existsByDisciplinas_Disciplina_Id(UUID disciplinaId);
}
