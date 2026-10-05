package com.projetosft.labdois.modules.matricula.domain;

import com.projetosft.labdois.modules.aluno.domain.Aluno;
import com.projetosft.labdois.modules.disciplina.domain.Disciplina;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "matriculas")
public class Matricula {

    public static final int MAX_OBRIGATORIAS = 4;
    public static final int MAX_OPTATIVAS = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String periodo;

    private LocalDate data;

    @ManyToOne(optional = false)
    private Aluno aluno;

    @OneToMany(mappedBy = "matricula", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<MatriculaDisciplina> disciplinas = new ArrayList<>();

    protected Matricula() {
    }

    public Matricula(String periodo, LocalDate data, Aluno aluno) {
        this.periodo = periodo;
        this.data = data;
        this.aluno = aluno;
    }

    public void adicionarDisciplina(Disciplina disciplina, TipoDisciplinaMatricula tipo) {
        disciplinas.add(new MatriculaDisciplina(this, disciplina, tipo));
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
        return (int) disciplinas.stream()
                .filter(item -> item.getTipo() == TipoDisciplinaMatricula.OBRIGATORIA)
                .count();
    }

    public int getTotalOptativas() {
        return (int) disciplinas.stream()
                .filter(item -> item.getTipo() == TipoDisciplinaMatricula.OPTATIVA)
                .count();
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public List<MatriculaDisciplina> getDisciplinas() {
        return disciplinas;
    }
}
