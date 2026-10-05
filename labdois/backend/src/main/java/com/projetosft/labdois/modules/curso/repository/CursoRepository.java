package com.projetosft.labdois.modules.curso.repository;

import com.projetosft.labdois.modules.curso.domain.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CursoRepository extends JpaRepository<Curso, UUID> {

    Optional<Curso> findByNome(String nome);

    Optional<Curso> findByNomeAndIdNot(String nome, UUID id);
}
