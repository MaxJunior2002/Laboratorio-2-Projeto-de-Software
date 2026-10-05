package com.projetosft.labdois.modules.disciplina.domain;

import com.projetosft.labdois.modules.matricula.domain.PeriodoInscricao;
import com.projetosft.labdois.modules.matricula.domain.MatriculaDisciplina;
import com.projetosft.labdois.modules.professor.domain.Professor;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ofertas_disciplinas", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"disciplina_id", "periodo_inscricao_id"})
})
public class OfertaDisciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "disciplina_id", nullable = false)
    private Disciplina disciplina;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "periodo_inscricao_id", nullable = false)
    private PeriodoInscricao periodoInscricao;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    private int capacidadeMaxima = 60;

    private int minimoAlunos = 3;

    @Enumerated(EnumType.STRING)
    private StatusDisciplina status = StatusDisciplina.ABERTA;

    @OneToMany(mappedBy = "oferta")
    private List<MatriculaDisciplina> matriculas = new ArrayList<>();

    protected OfertaDisciplina() {
    }

    public OfertaDisciplina(
            Disciplina disciplina, PeriodoInscricao periodoInscricao, Professor professor,
            int capacidadeMaxima, int minimoAlunos) {
        this.disciplina = disciplina;
        this.periodoInscricao = periodoInscricao;
        this.professor = professor;
        this.capacidadeMaxima = capacidadeMaxima;
        this.minimoAlunos = minimoAlunos;
    }

    public UUID getId() {
        return id;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public PeriodoInscricao getPeriodoInscricao() {
        return periodoInscricao;
    }

    public Professor getProfessor() {
        return professor;
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

    public StatusDisciplina getStatus() {
        return status;
    }

    public void setStatus(StatusDisciplina status) {
        this.status = status;
    }

    public List<MatriculaDisciplina> getMatriculas() {
        return matriculas;
    }
}
