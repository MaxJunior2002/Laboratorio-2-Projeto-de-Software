package com.projetosft.labdois.modules.aluno.service;

import com.projetosft.labdois.modules.aluno.domain.Aluno;
import com.projetosft.labdois.modules.aluno.repository.AlunoRepository;
import com.projetosft.labdois.modules.professor.repository.ProfessorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;

    public AlunoService(AlunoRepository alunoRepository, ProfessorRepository professorRepository) {
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
    }

    public Aluno criar(String nome, String email, String senha, String numeroMatricula) {
        validarDados(nome, email, senha);
        String emailNormalizado = email.trim();
        if (alunoRepository.findByEmail(emailNormalizado).isPresent()
                || professorRepository.findByEmail(emailNormalizado).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado.");
        }

        return alunoRepository.save(new Aluno(nome.trim(), emailNormalizado, senha, numeroMatricula.trim()));
    }

    private void validarDados(String nome, String email, String senha) {
        if (nome == null || nome.isBlank() || email == null || email.isBlank() || senha == null || senha.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome, email e senha são obrigatórios.");
        }
    }
}