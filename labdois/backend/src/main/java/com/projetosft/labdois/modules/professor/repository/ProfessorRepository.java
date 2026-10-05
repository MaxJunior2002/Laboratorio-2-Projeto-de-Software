package com.projetosft.labdois.modules.professor.repository;

import com.projetosft.labdois.modules.professor.domain.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProfessorRepository extends JpaRepository<Professor, UUID> {

    Optional<Professor> findByEmail(String email);
}