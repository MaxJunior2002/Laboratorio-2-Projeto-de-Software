package com.projetosft.labdois.modules.auth.dto;

import com.projetosft.labdois.modules.aluno.domain.Aluno;
import com.projetosft.labdois.modules.professor.domain.Professor;

import java.util.UUID;

public class LoginResponse {

    private UUID id;
    private String nome;
    private String email;
    private String perfil;

    public LoginResponse() {
    }

    public LoginResponse(UUID id, String nome, String email, String perfil) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.perfil = perfil;
    }

    public static LoginResponse from(Aluno aluno) {
        return new LoginResponse(
                aluno.getId(), aluno.getNome(), aluno.getEmail(), "Aluno");
    }

    public static LoginResponse from(Professor professor) {
        return new LoginResponse(
                professor.getId(), professor.getNome(), professor.getEmail(), "Professor");
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getPerfil() {
        return perfil;
    }
}
