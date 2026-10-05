package com.projetosft.labdois.modules.professor.service;

import com.projetosft.labdois.modules.aluno.repository.AlunoRepository;
import com.projetosft.labdois.modules.professor.domain.Professor;
import com.projetosft.labdois.modules.professor.repository.ProfessorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final AlunoRepository alunoRepository;

    public ProfessorService(ProfessorRepository professorRepository, AlunoRepository alunoRepository) {
        this.professorRepository = professorRepository;
        this.alunoRepository = alunoRepository;
    }

    public Professor criar(String nome, String email, String senha, String identificadorFuncional) {
        validarDados(nome, email, senha);
        String emailNormalizado = email.trim();
        if (professorRepository.findByEmail(emailNormalizado).isPresent()
                || alunoRepository.findByEmail(emailNormalizado).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado.");
        }

        return professorRepository.save(new Professor(nome.trim(), emailNormalizado, senha, identificadorFuncional.trim()));
    }

    private void validarDados(String nome, String email, String senha) {
        if (nome == null || nome.isBlank() || email == null || email.isBlank() || senha == null || senha.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome, email e senha são obrigatórios.");
        }
    }
}