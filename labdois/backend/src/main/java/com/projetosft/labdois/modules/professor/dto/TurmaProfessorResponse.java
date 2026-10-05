package com.projetosft.labdois.modules.professor.dto;

import com.projetosft.labdois.modules.aluno.domain.Aluno;
import com.projetosft.labdois.modules.disciplina.domain.OfertaDisciplina;
import com.projetosft.labdois.modules.matricula.domain.MatriculaDisciplina;

import java.util.List;
import java.util.UUID;

public record TurmaProfessorResponse(
        UUID ofertaId,
        UUID disciplinaId,
        String disciplinaNome,
        String periodo,
        List<AlunoMatriculado> alunos
) {

    public static TurmaProfessorResponse from(OfertaDisciplina oferta, List<MatriculaDisciplina> matriculas) {
        List<AlunoMatriculado> alunos = matriculas.stream()
                .filter(item -> item.getOferta().getId().equals(oferta.getId()))
                .map(item -> item.getMatricula().getAluno())
                .distinct()
                .map(AlunoMatriculado::from)
                .toList();
        return new TurmaProfessorResponse(
                oferta.getId(),
                oferta.getDisciplina().getId(),
                oferta.getDisciplina().getNome(),
                oferta.getPeriodoInscricao().getPeriodo(),
                alunos);
    }

    public record AlunoMatriculado(UUID id, String nome, String email, String numeroMatricula) {

        public static AlunoMatriculado from(Aluno aluno) {
            return new AlunoMatriculado(
                    aluno.getId(), aluno.getNome(), aluno.getEmail(), aluno.getNumeroMatricula());
        }
    }
}
