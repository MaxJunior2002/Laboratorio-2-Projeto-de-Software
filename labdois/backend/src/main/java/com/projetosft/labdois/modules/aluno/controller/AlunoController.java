package com.projetosft.labdois.modules.aluno.controller;

import com.projetosft.labdois.modules.aluno.domain.Aluno;
import com.projetosft.labdois.modules.aluno.dto.CriarAlunoRequest;
import com.projetosft.labdois.modules.aluno.service.AlunoService;
import com.projetosft.labdois.modules.auth.dto.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alunos")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @PostMapping
    public ResponseEntity<LoginResponse> criar(@Valid @RequestBody CriarAlunoRequest request) {
        Aluno aluno = alunoService.criar(
                request.getNome(), request.getEmail(), request.getSenha(), request.getNumeroMatricula());
        return ResponseEntity.status(HttpStatus.CREATED).body(LoginResponse.from(aluno));
    }
}