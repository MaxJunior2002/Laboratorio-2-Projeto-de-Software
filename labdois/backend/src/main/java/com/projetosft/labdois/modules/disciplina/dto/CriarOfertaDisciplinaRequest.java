package com.projetosft.labdois.modules.disciplina.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CriarOfertaDisciplinaRequest {

    @NotNull
    private UUID disciplinaId;

    @NotBlank
    private String periodo;

    @NotNull
    private UUID professorId;

    @Min(1)
    private int capacidadeMaxima = 60;

    @Min(1)
    private int minimoAlunos = 3;

    public UUID getDisciplinaId() {
        return disciplinaId;
    }

    public void setDisciplinaId(UUID disciplinaId) {
        this.disciplinaId = disciplinaId;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public UUID getProfessorId() {
        return professorId;
    }

    public void setProfessorId(UUID professorId) {
        this.professorId = professorId;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public void setCapacidadeMaxima(int capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public int getMinimoAlunos() {
        return minimoAlunos;
    }

    public void setMinimoAlunos(int minimoAlunos) {
        this.minimoAlunos = minimoAlunos;
    }
}
