package com.projetosft.labdois.modules.matricula.dto;

import com.projetosft.labdois.modules.matricula.domain.Matricula;
import com.projetosft.labdois.modules.matricula.domain.StatusMatricula;
import com.projetosft.labdois.modules.matricula.domain.TipoDisciplinaMatricula;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record MatriculaResponse(
        UUID id,
        UUID alunoId,
        String periodo,
        LocalDate data,
        LocalDate dataCancelamento,
        StatusMatricula status,
        List<DisciplinaSelecionada> disciplinas,
        int totalObrigatorias,
        int totalOptativas
) {

    public static MatriculaResponse from(Matricula matricula) {
        List<DisciplinaSelecionada> disciplinas = matricula.getDisciplinas().stream()
                .map(item -> new DisciplinaSelecionada(
                        item.getOferta().getId(),
                        item.getOferta().getDisciplina().getId(),
                        item.getOferta().getDisciplina().getNome(),
                        item.getOferta().getProfessor().getId(),
                        item.getOferta().getProfessor().getNome(),
                        item.getOferta().getPeriodoInscricao().getPeriodo(),
                        item.getTipo()))
                .toList();
        return new MatriculaResponse(
                matricula.getId(),
                matricula.getAluno().getId(),
                matricula.getPeriodo(),
                matricula.getData(),
                matricula.getDataCancelamento(),
                matricula.getStatus(),
                disciplinas,
                matricula.getTotalObrigatorias(),
                matricula.getTotalOptativas());
    }

    public record DisciplinaSelecionada(
            UUID ofertaId, UUID disciplinaId, String nome, UUID professorId,
            String professorNome, String periodo, TipoDisciplinaMatricula tipo) {
    }
}
