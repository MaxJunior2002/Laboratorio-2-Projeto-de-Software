package com.projetosft.labdois.modules.aluno.repository;

import com.projetosft.labdois.modules.aluno.domain.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AlunoRepository extends JpaRepository<Aluno, UUID> {

    Optional<Aluno> findByEmail(String email);
}