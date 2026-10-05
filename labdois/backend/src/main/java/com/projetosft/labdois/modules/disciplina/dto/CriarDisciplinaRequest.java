package com.projetosft.labdois.modules.disciplina.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CriarDisciplinaRequest {

    @NotBlank
    private String nome;

    @Min(1)
    private int cargaHoraria;

    @NotNull
    private UUID cursoId;

    @NotNull
    private UUID professorId;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(int cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public UUID getCursoId() {
        return cursoId;
    }

    public void setCursoId(UUID cursoId) {
        this.cursoId = cursoId;
    }

    public UUID getProfessorId() {
        return professorId;
    }

    public void setProfessorId(UUID professorId) {
        this.professorId = professorId;
    }
}
