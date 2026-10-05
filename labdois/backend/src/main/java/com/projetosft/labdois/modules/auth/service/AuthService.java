package com.projetosft.labdois.modules.auth.service;

import com.projetosft.labdois.modules.auth.dto.LoginRequest;
import com.projetosft.labdois.modules.auth.dto.LoginResponse;
import com.projetosft.labdois.modules.aluno.domain.Aluno;
import com.projetosft.labdois.modules.aluno.repository.AlunoRepository;
import com.projetosft.labdois.modules.professor.domain.Professor;
import com.projetosft.labdois.modules.professor.repository.ProfessorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;

    public AuthService(AlunoRepository alunoRepository, ProfessorRepository professorRepository) {
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
    }

    public LoginResponse autenticar(LoginRequest request) {
        if (request == null || request.getEmail() == null || request.getSenha() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas.");
        }

        String email = request.getEmail().trim();
        Aluno aluno = alunoRepository.findByEmail(email).orElse(null);
        Professor professor = professorRepository.findByEmail(email).orElse(null);

        if (aluno != null && professor == null && request.getSenha().equals(aluno.getSenha())) {
            return LoginResponse.from(aluno);
        }
        if (professor != null && aluno == null && request.getSenha().equals(professor.getSenha())) {
            return LoginResponse.from(professor);
        }
        if (aluno == null && professor == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas.");
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas.");
    }
}
