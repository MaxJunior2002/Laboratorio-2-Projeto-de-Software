package com.projetosft.labdois.modules.disciplina.repository;

import com.projetosft.labdois.modules.disciplina.domain.Disciplina;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DisciplinaRepository extends JpaRepository<Disciplina, UUID> {

    boolean existsByCurso_Id(UUID cursoId);
}
