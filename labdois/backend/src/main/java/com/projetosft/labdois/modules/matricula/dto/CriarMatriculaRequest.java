package com.projetosft.labdois.modules.matricula.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
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
    @JsonAlias("disciplinasObrigatorias")
    private List<UUID> ofertasObrigatorias;

    @NotNull
    @JsonAlias("disciplinasOptativas")
    private List<UUID> ofertasOptativas;

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

    public List<UUID> getOfertasObrigatorias() {
        return ofertasObrigatorias;
    }

    public void setOfertasObrigatorias(List<UUID> ofertasObrigatorias) {
        this.ofertasObrigatorias = ofertasObrigatorias;
    }

    public List<UUID> getOfertasOptativas() {
        return ofertasOptativas;
    }

    public void setOfertasOptativas(List<UUID> ofertasOptativas) {
        this.ofertasOptativas = ofertasOptativas;
    }
}
