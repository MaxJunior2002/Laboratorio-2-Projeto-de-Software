package com.projetosft.labdois.modules.aluno.domain;

import com.projetosft.labdois.modules.matricula.domain.Matricula;
import com.projetosft.labdois.modules.usuario.domain.Usuario;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "alunos")
public class Aluno extends Usuario {

    private String numeroMatricula;

    @OneToMany(mappedBy = "aluno")
    private List<Matricula> matriculas = new ArrayList<>();

    protected Aluno() {
    }

    public Aluno(String nome, String email, String senha, String numeroMatricula) {
        super(nome, email, senha);
        this.numeroMatricula = numeroMatricula;
    }

    public String getNumeroMatricula() {
        return numeroMatricula;
    }

    public void setNumeroMatricula(String numeroMatricula) {
        this.numeroMatricula = numeroMatricula;
    }

    public List<Matricula> getMatriculas() {
        return matriculas;
    }
}