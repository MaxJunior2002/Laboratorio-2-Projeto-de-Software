package com.projetosft.labdois.modules.disciplina.domain;

import com.projetosft.labdois.modules.curso.domain.Curso;
import com.projetosft.labdois.modules.matricula.domain.Matricula;
import com.projetosft.labdois.modules.professor.domain.Professor;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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

    private int capacidadeMaxima = 60;

    private int minimoAlunos = 3;

    @Enumerated(EnumType.STRING)
    private StatusDisciplina status = StatusDisciplina.ABERTA;

    @ManyToOne
    private Curso curso;

    @ManyToOne
    private Professor professor;

    @ManyToMany(mappedBy = "disciplinas")
    private List<Matricula> matriculas = new ArrayList<>();

    protected Disciplina() {
    }

    public Disciplina(String nome, int cargaHoraria, Curso curso, Professor professor) {
        this.nome = nome;
        this.cargaHoraria = cargaHoraria;
        this.curso = curso;
        this.professor = professor;
    }

    public boolean verificarAtivacao() {
        long alunosInscritos = matriculas.stream()
                .map(Matricula::getAluno)
                .distinct()
                .count();
        return alunosInscritos >= minimoAlunos;
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

    public StatusDisciplina getStatus() {
        return status;
    }

    public void setStatus(StatusDisciplina status) {
        this.status = status;
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

    public List<Matricula> getMatriculas() {
        return matriculas;
    }
}