package com.projetosft.labdois.modules.auth.dto;

import com.projetosft.labdois.modules.usuario.domain.Usuario;

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

    public static LoginResponse from(Usuario usuario) {
        return new LoginResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getClass().getSimpleName()
        );
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
