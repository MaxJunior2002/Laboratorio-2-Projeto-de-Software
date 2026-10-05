package com.projetosft.labdois.modules.disciplina.dto;

import com.projetosft.labdois.modules.disciplina.domain.OfertaDisciplina;
import com.projetosft.labdois.modules.disciplina.domain.StatusDisciplina;

import java.util.UUID;

public record OfertaDisciplinaResponse(
        UUID id,
        UUID disciplinaId,
        String disciplinaNome,
        String periodo,
        UUID professorId,
        String professorNome,
        int capacidadeMaxima,
        int minimoAlunos,
        StatusDisciplina status
) {

    public static OfertaDisciplinaResponse from(OfertaDisciplina oferta) {
        return new OfertaDisciplinaResponse(
                oferta.getId(),
                oferta.getDisciplina().getId(),
                oferta.getDisciplina().getNome(),
                oferta.getPeriodoInscricao().getPeriodo(),
                oferta.getProfessor().getId(),
                oferta.getProfessor().getNome(),
                oferta.getCapacidadeMaxima(),
                oferta.getMinimoAlunos(),
                oferta.getStatus());
    }
}
