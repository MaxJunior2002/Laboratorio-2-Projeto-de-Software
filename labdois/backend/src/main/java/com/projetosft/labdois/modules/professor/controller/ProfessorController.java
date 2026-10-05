package com.projetosft.labdois.modules.professor.controller;

import com.projetosft.labdois.modules.auth.dto.LoginResponse;
import com.projetosft.labdois.modules.professor.domain.Professor;
import com.projetosft.labdois.modules.professor.dto.CriarProfessorRequest;
import com.projetosft.labdois.modules.professor.service.ProfessorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/professores")
public class ProfessorController {

    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @PostMapping
    public ResponseEntity<LoginResponse> criar(@Valid @RequestBody CriarProfessorRequest request) {
        Professor professor = professorService.criar(
                request.getNome(), request.getEmail(), request.getSenha(), request.getIdentificadorFuncional());
        return ResponseEntity.status(HttpStatus.CREATED).body(LoginResponse.from(professor));
    }
}