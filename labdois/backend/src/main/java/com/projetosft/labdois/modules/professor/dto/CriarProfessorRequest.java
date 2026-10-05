package com.projetosft.labdois.modules.professor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CriarProfessorRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    private String senha;

    @NotBlank(message = "Identificador funcional é obrigatório")
    private String identificadorFuncional;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getIdentificadorFuncional() { return identificadorFuncional; }
    public void setIdentificadorFuncional(String identificadorFuncional) { this.identificadorFuncional = identificadorFuncional; }
}