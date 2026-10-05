package com.projetosft.labdois.modules.matricula.repository;

import com.projetosft.labdois.modules.matricula.domain.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MatriculaRepository extends JpaRepository<Matricula, UUID> {

    boolean existsByDisciplinas_Id(UUID disciplinaId);
}
