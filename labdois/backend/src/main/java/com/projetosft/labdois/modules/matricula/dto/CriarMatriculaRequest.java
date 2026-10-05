package com.projetosft.labdois.modules.matricula.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public class CriarMatriculaRequest {

    @NotNull
    private UUID alunoId;

    @NotBlank
    private String periodo;

    @NotNull
    private List<UUID> disciplinasObrigatorias;

    @NotNull
    private List<UUID> disciplinasOptativas;

    public UUID getAlunoId() {
        return alunoId;
    }

    public void setAlunoId(UUID alunoId) {
        this.alunoId = alunoId;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public List<UUID> getDisciplinasObrigatorias() {
        return disciplinasObrigatorias;
    }

    public void setDisciplinasObrigatorias(List<UUID> disciplinasObrigatorias) {
        this.disciplinasObrigatorias = disciplinasObrigatorias;
    }

    public List<UUID> getDisciplinasOptativas() {
        return disciplinasOptativas;
    }

    public void setDisciplinasOptativas(List<UUID> disciplinasOptativas) {
        this.disciplinasOptativas = disciplinasOptativas;
    }
}
