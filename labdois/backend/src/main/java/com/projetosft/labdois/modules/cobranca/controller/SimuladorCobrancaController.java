package com.projetosft.labdois.modules.cobranca.controller;

import com.projetosft.labdois.modules.cobranca.dto.NotificacaoCobrancaResponse;
import com.projetosft.labdois.modules.cobranca.service.SimuladorCobrancaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/cobrancas/matriculas")
public class SimuladorCobrancaController {

    private final SimuladorCobrancaService simuladorCobrancaService;

    public SimuladorCobrancaController(SimuladorCobrancaService simuladorCobrancaService) {
        this.simuladorCobrancaService = simuladorCobrancaService;
    }

    @GetMapping("/{matriculaId}")
    public NotificacaoCobrancaResponse buscarPorMatricula(@PathVariable UUID matriculaId) {
        return simuladorCobrancaService.buscarPorMatricula(matriculaId);
    }
}
