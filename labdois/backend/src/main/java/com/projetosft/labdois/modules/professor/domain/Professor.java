package com.projetosft.labdois.modules.professor.domain;

import com.projetosft.labdois.modules.disciplina.domain.Disciplina;
import com.projetosft.labdois.modules.usuario.domain.Usuario;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "professores")
public class Professor extends Usuario {

    private String identificadorFuncional;

    @OneToMany(mappedBy = "professor")
    private List<Disciplina> disciplinas = new ArrayList<>();

    protected Professor() {
    }

    public Professor(String nome, String email, String senha, String identificadorFuncional) {
        super(nome, email, senha);
        this.identificadorFuncional = identificadorFuncional;
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