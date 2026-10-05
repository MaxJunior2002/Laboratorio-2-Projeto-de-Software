package com.projetosft.labdois.modules.matricula.dto;

import com.projetosft.labdois.modules.matricula.service.PeriodoInscricaoService;

public record EncerramentoPeriodoResponse(
        String periodo,
        int disciplinasAtivadas,
        int disciplinasCanceladas
) {

    public static EncerramentoPeriodoResponse from(PeriodoInscricaoService.ResumoEncerramento resumo) {
        return new EncerramentoPeriodoResponse(
                resumo.periodo(),
                resumo.disciplinasAtivadas(),
                resumo.disciplinasCanceladas());
    }
}
