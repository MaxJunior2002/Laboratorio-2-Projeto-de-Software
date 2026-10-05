package com.projetosft.labdois.modules.matricula.dto;

import com.projetosft.labdois.modules.matricula.domain.Matricula;
import com.projetosft.labdois.modules.matricula.domain.TipoDisciplinaMatricula;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record MatriculaResponse(
        UUID id,
        UUID alunoId,
        String periodo,
        LocalDate data,
        List<DisciplinaSelecionada> disciplinas,
        int totalObrigatorias,
        int totalOptativas
) {

    public static MatriculaResponse from(Matricula matricula) {
        List<DisciplinaSelecionada> disciplinas = matricula.getDisciplinas().stream()
                .map(item -> new DisciplinaSelecionada(
                        item.getDisciplina().getId(),
                        item.getDisciplina().getNome(),
                        item.getTipo()))
                .toList();
        return new MatriculaResponse(
                matricula.getId(),
                matricula.getAluno().getId(),
                matricula.getPeriodo(),
                matricula.getData(),
                disciplinas,
                matricula.getTotalObrigatorias(),
                matricula.getTotalOptativas());
    }

    public record DisciplinaSelecionada(UUID disciplinaId, String nome, TipoDisciplinaMatricula tipo) {
    }
}
