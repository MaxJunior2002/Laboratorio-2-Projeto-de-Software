package com.projetosft.labdois.modules.disciplina.domain;

import com.projetosft.labdois.modules.curso.domain.Curso;
import com.projetosft.labdois.modules.professor.domain.Professor;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "disciplinas")
public class Disciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String nome;

    private int cargaHoraria;

    @ManyToOne
    private Curso curso;

    @ManyToOne
    private Professor professor;

    @OneToMany(mappedBy = "disciplina")
    private List<OfertaDisciplina> ofertas = new ArrayList<>();

    protected Disciplina() {
    }

    public Disciplina(String nome, int cargaHoraria, Curso curso, Professor professor) {
        this.nome = nome;
        this.cargaHoraria = cargaHoraria;
        this.curso = curso;
        this.professor = professor;
    }

    public UUID getId() {
        return id;
    }

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

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public List<OfertaDisciplina> getOfertas() {
        return ofertas;
    }
}