package com.projetosft.labdois.modules.disciplina.controller;

import com.projetosft.labdois.modules.disciplina.domain.Disciplina;
import com.projetosft.labdois.modules.disciplina.dto.CriarDisciplinaRequest;
import com.projetosft.labdois.modules.disciplina.dto.DisciplinaResponse;
import com.projetosft.labdois.modules.disciplina.service.DisciplinaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/disciplinas")
public class DisciplinaController {

    private final DisciplinaService disciplinaService;

    public DisciplinaController(DisciplinaService disciplinaService) {
        this.disciplinaService = disciplinaService;
    }

    @PostMapping
    public ResponseEntity<DisciplinaResponse> criar(@Valid @RequestBody CriarDisciplinaRequest request) {
        Disciplina disciplina = disciplinaService.criar(
                request.getNome(), request.getCargaHoraria(), request.getCursoId(), request.getProfessorId());
        return ResponseEntity.status(HttpStatus.CREATED).body(DisciplinaResponse.from(disciplina));
    }

    @GetMapping
    public List<DisciplinaResponse> listar() {
        return disciplinaService.listar().stream().map(DisciplinaResponse::from).toList();
    }

    @GetMapping("/{id}")
    public DisciplinaResponse buscar(@PathVariable UUID id) {
        return DisciplinaResponse.from(disciplinaService.buscar(id));
    }

    @PutMapping("/{id}")
    public DisciplinaResponse atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody CriarDisciplinaRequest request) {
        return DisciplinaResponse.from(disciplinaService.atualizar(
                id, request.getNome(), request.getCargaHoraria(), request.getCursoId(), request.getProfessorId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        disciplinaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
