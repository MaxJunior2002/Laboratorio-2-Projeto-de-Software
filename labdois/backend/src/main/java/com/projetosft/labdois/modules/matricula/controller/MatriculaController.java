package com.projetosft.labdois.modules.matricula.controller;

import com.projetosft.labdois.modules.matricula.domain.Matricula;
import com.projetosft.labdois.modules.matricula.dto.CriarMatriculaRequest;
import com.projetosft.labdois.modules.matricula.dto.MatriculaResponse;
import com.projetosft.labdois.modules.matricula.service.MatriculaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/matriculas")
public class MatriculaController {

    private final MatriculaService matriculaService;

    public MatriculaController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    @PostMapping
    public ResponseEntity<MatriculaResponse> criar(@Valid @RequestBody CriarMatriculaRequest request) {
        Matricula matricula = matriculaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(MatriculaResponse.from(matricula));
    }

    @GetMapping("/{id}")
    public MatriculaResponse buscar(@PathVariable UUID id) {
        return MatriculaResponse.from(matriculaService.buscar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable UUID id) {
        matriculaService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}
