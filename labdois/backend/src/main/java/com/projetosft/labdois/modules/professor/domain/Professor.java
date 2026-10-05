package com.projetosft.labdois.modules.professor.domain;

import com.projetosft.labdois.modules.disciplina.domain.Disciplina;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "professores")
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    private String identificadorFuncional;

    @OneToMany(mappedBy = "professor")
    private List<Disciplina> disciplinas = new ArrayList<>();

    protected Professor() {
    }

    public Professor(String nome, String email, String senha, String identificadorFuncional) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.identificadorFuncional = identificadorFuncional;
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

    public String getSenha() {
        return senha;
    }

    public String getIdentificadorFuncional() {
        return identificadorFuncional;
    }

    public void setIdentificadorFuncional(String identificadorFuncional) {
        this.identificadorFuncional = identificadorFuncional;
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }
}