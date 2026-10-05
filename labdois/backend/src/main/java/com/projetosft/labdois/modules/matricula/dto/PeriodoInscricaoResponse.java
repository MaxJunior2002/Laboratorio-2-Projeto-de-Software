package com.projetosft.labdois.modules.matricula.dto;

import com.projetosft.labdois.modules.matricula.domain.PeriodoInscricao;

import java.time.LocalDate;
import java.util.UUID;

public record PeriodoInscricaoResponse(UUID id, String periodo, LocalDate inicio, LocalDate fim) {

    public static PeriodoInscricaoResponse from(PeriodoInscricao periodoInscricao) {
        return new PeriodoInscricaoResponse(
                periodoInscricao.getId(),
                periodoInscricao.getPeriodo(),
                periodoInscricao.getInicio(),
                periodoInscricao.getFim());
    }
}
