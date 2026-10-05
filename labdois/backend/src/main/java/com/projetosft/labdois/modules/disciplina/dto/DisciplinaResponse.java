package com.projetosft.labdois.modules.disciplina.dto;

import com.projetosft.labdois.modules.disciplina.domain.Disciplina;
import com.projetosft.labdois.modules.disciplina.domain.StatusDisciplina;

import java.util.UUID;

public record DisciplinaResponse(
        UUID id,
        String nome,
        int cargaHoraria,
        int capacidadeMaxima,
        int minimoAlunos,
        StatusDisciplina status,
        UUID cursoId,
        String cursoNome,
        UUID professorId,
        String professorNome
) {

    public static DisciplinaResponse from(Disciplina disciplina) {
        return new DisciplinaResponse(
                disciplina.getId(),
                disciplina.getNome(),
                disciplina.getCargaHoraria(),
                disciplina.getCapacidadeMaxima(),
                disciplina.getMinimoAlunos(),
                disciplina.getStatus(),
                disciplina.getCurso().getId(),
                disciplina.getCurso().getNome(),
                disciplina.getProfessor().getId(),
                disciplina.getProfessor().getNome()
        );
    }
}
