package com.projetosft.labdois.modules.cobranca.dto;

import com.projetosft.labdois.modules.cobranca.domain.NotificacaoCobranca;
import com.projetosft.labdois.modules.cobranca.domain.StatusCobranca;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificacaoCobrancaResponse(
        UUID id,
        UUID matriculaId,
        UUID alunoId,
        String periodo,
        int quantidadeDisciplinas,
        LocalDateTime solicitadaEm,
        StatusCobranca status
) {

    public static NotificacaoCobrancaResponse from(NotificacaoCobranca notificacao) {
        var matricula = notificacao.getMatricula();
        return new NotificacaoCobrancaResponse(
                notificacao.getId(),
                matricula.getId(),
                matricula.getAluno().getId(),
                matricula.getPeriodo(),
                notificacao.getQuantidadeDisciplinas(),
                notificacao.getSolicitadaEm(),
                notificacao.getStatus());
    }
}
