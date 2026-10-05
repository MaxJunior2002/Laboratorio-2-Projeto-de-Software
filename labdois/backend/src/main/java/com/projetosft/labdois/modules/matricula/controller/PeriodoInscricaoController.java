package com.projetosft.labdois.modules.matricula.controller;

import com.projetosft.labdois.modules.matricula.domain.PeriodoInscricao;
import com.projetosft.labdois.modules.matricula.dto.PeriodoInscricaoRequest;
import com.projetosft.labdois.modules.matricula.dto.PeriodoInscricaoResponse;
import com.projetosft.labdois.modules.matricula.dto.EncerramentoPeriodoResponse;
import com.projetosft.labdois.modules.matricula.service.PeriodoInscricaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/periodos-inscricao")
public class PeriodoInscricaoController {

    private final PeriodoInscricaoService periodoInscricaoService;

    public PeriodoInscricaoController(PeriodoInscricaoService periodoInscricaoService) {
        this.periodoInscricaoService = periodoInscricaoService;
    }

    @PostMapping
    public ResponseEntity<PeriodoInscricaoResponse> criar(
            @Valid @RequestBody PeriodoInscricaoRequest request) {
        PeriodoInscricao periodo = periodoInscricaoService.criar(
                request.getPeriodo(), request.getInicio(), request.getFim());
        return ResponseEntity.status(HttpStatus.CREATED).body(PeriodoInscricaoResponse.from(periodo));
    }

    @PutMapping("/{periodo}")
    public PeriodoInscricaoResponse atualizar(
            @PathVariable String periodo,
            @Valid @RequestBody PeriodoInscricaoRequest request) {
        if (!periodo.equals(request.getPeriodo())) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "O período informado na URL e no corpo deve ser o mesmo.");
        }
        return PeriodoInscricaoResponse.from(periodoInscricaoService.atualizar(
                periodo, request.getInicio(), request.getFim()));
    }

    @GetMapping
    public List<PeriodoInscricaoResponse> listar() {
        return periodoInscricaoService.listar().stream().map(PeriodoInscricaoResponse::from).toList();
    }

    @GetMapping("/{periodo}")
    public PeriodoInscricaoResponse buscar(@PathVariable String periodo) {
        return PeriodoInscricaoResponse.from(periodoInscricaoService.buscar(periodo));
    }

    @PostMapping("/{periodo}/encerrar")
    public EncerramentoPeriodoResponse encerrar(@PathVariable String periodo) {
        return EncerramentoPeriodoResponse.from(periodoInscricaoService.encerrar(periodo, LocalDate.now()));
    }
}
