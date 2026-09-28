package com.projetosft.labdois.modules.matricula.domain;

import com.projetosft.labdois.modules.aluno.domain.Aluno;
import com.projetosft.labdois.modules.disciplina.domain.Disciplina;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "matriculas")
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String periodo;

    private LocalDate data;

    private int totalObrigatorias = 4;

    private int totalOptativas = 2;

    @ManyToOne(optional = false)
    private Aluno aluno;

    @ManyToMany
    @JoinTable(
            name = "matricula_disciplinas",
            joinColumns = @JoinColumn(name = "matricula_id"),
            inverseJoinColumns = @JoinColumn(name = "disciplina_id")
    )
    private List<Disciplina> disciplinas = new ArrayList<>();

    protected Matricula() {
    }

    public Matricula(String periodo, LocalDate data, Aluno aluno) {
        this.periodo = periodo;
        this.data = data;
        this.aluno = aluno;
    }

    public void confirmar() {
        if (disciplinas.isEmpty()) {
            throw new IllegalStateException("A matrícula precisa conter ao menos uma disciplina.");
        }
    }

    public void cancelar() {
        disciplinas.clear();
    }

    public UUID getId() {
        return id;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public int getTotalObrigatorias() {
        return totalObrigatorias;
    }

    public int getTotalOptativas() {
        return totalOptativas;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }
}