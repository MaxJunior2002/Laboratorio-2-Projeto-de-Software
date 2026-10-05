package com.projetosft.labdois.modules.professor.controller;

import com.projetosft.labdois.modules.auth.dto.LoginResponse;
import com.projetosft.labdois.modules.professor.domain.Professor;
import com.projetosft.labdois.modules.professor.dto.CriarProfessorRequest;
import com.projetosft.labdois.modules.professor.dto.TurmaProfessorResponse;
import com.projetosft.labdois.modules.professor.service.ProfessorService;
import com.projetosft.labdois.modules.professor.service.TurmaProfessorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/professores")
public class ProfessorController {

    private final ProfessorService professorService;
    private final TurmaProfessorService turmaProfessorService;

    public ProfessorController(ProfessorService professorService, TurmaProfessorService turmaProfessorService) {
        this.professorService = professorService;
        this.turmaProfessorService = turmaProfessorService;
    }

    @PostMapping
    public ResponseEntity<LoginResponse> criar(@Valid @RequestBody CriarProfessorRequest request) {
        Professor professor = professorService.criar(
                request.getNome(), request.getEmail(), request.getSenha(), request.getIdentificadorFuncional());
        return ResponseEntity.status(HttpStatus.CREATED).body(LoginResponse.from(professor));
    }

    @GetMapping("/{professorId}/turmas")
    public List<TurmaProfessorResponse> listarTurmas(
            @PathVariable UUID professorId,
            @RequestParam String periodo) {
        return turmaProfessorService.listarTurmas(professorId, periodo);
    }
}