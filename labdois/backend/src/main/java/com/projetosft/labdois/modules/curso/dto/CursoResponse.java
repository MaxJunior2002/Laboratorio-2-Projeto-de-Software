package com.projetosft.labdois.modules.curso.dto;

import com.projetosft.labdois.modules.curso.domain.Curso;

import java.util.UUID;

public record CursoResponse(UUID id, String nome, int numeroCreditos) {

    public static CursoResponse from(Curso curso) {
        return new CursoResponse(curso.getId(), curso.getNome(), curso.getNumeroCreditos());
    }
}
