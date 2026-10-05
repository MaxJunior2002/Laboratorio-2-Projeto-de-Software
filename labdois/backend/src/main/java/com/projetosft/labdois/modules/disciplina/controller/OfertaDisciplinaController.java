package com.projetosft.labdois.modules.disciplina.controller;

import com.projetosft.labdois.modules.disciplina.domain.OfertaDisciplina;
import com.projetosft.labdois.modules.disciplina.dto.CriarOfertaDisciplinaRequest;
import com.projetosft.labdois.modules.disciplina.dto.OfertaDisciplinaResponse;
import com.projetosft.labdois.modules.disciplina.service.OfertaDisciplinaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ofertas-disciplinas")
public class OfertaDisciplinaController {

    private final OfertaDisciplinaService ofertaService;

    public OfertaDisciplinaController(OfertaDisciplinaService ofertaService) {
        this.ofertaService = ofertaService;
    }

    @PostMapping
    public ResponseEntity<OfertaDisciplinaResponse> criar(
            @Valid @RequestBody CriarOfertaDisciplinaRequest request) {
        OfertaDisciplina oferta = ofertaService.criar(
                request.getDisciplinaId(), request.getPeriodo(), request.getProfessorId(),
                request.getCapacidadeMaxima(), request.getMinimoAlunos());
        return ResponseEntity.status(HttpStatus.CREATED).body(OfertaDisciplinaResponse.from(oferta));
    }

    @GetMapping
    public List<OfertaDisciplinaResponse> listar(@RequestParam String periodo) {
        return ofertaService.listarPorPeriodo(periodo).stream().map(OfertaDisciplinaResponse::from).toList();
    }

    @GetMapping("/{id}")
    public OfertaDisciplinaResponse buscar(@PathVariable UUID id) {
        return OfertaDisciplinaResponse.from(ofertaService.buscar(id));
    }
}
