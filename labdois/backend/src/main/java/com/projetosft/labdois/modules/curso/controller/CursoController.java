package com.projetosft.labdois.modules.curso.controller;

import com.projetosft.labdois.modules.curso.domain.Curso;
import com.projetosft.labdois.modules.curso.dto.CriarCursoRequest;
import com.projetosft.labdois.modules.curso.dto.CursoResponse;
import com.projetosft.labdois.modules.curso.service.CursoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @PostMapping
    public ResponseEntity<CursoResponse> criar(@Valid @RequestBody CriarCursoRequest request) {
        Curso curso = cursoService.criar(request.getNome(), request.getNumeroCreditos());
        return ResponseEntity.status(HttpStatus.CREATED).body(CursoResponse.from(curso));
    }

    @GetMapping
    public List<CursoResponse> listar() {
        return cursoService.listar().stream().map(CursoResponse::from).toList();
    }

    @GetMapping("/{id}")
    public CursoResponse buscar(@PathVariable UUID id) {
        return CursoResponse.from(cursoService.buscar(id));
    }

    @PutMapping("/{id}")
    public CursoResponse atualizar(@PathVariable UUID id, @Valid @RequestBody CriarCursoRequest request) {
        return CursoResponse.from(cursoService.atualizar(id, request.getNome(), request.getNumeroCreditos()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        cursoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
