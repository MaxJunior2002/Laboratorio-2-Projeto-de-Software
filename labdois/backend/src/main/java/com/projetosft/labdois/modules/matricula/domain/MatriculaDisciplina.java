package com.projetosft.labdois.modules.matricula.domain;

import com.projetosft.labdois.modules.disciplina.domain.OfertaDisciplina;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.UUID;

@Entity
@Table(name = "matricula_disciplina_selecionadas", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"matricula_id", "oferta_id"})
})
public class MatriculaDisciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "oferta_id", nullable = false)
    private OfertaDisciplina oferta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDisciplinaMatricula tipo;

    protected MatriculaDisciplina() {
    }

    public MatriculaDisciplina(Matricula matricula, OfertaDisciplina oferta, TipoDisciplinaMatricula tipo) {
        this.matricula = matricula;
        this.oferta = oferta;
        this.tipo = tipo;
    }

    public UUID getId() {
        return id;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public OfertaDisciplina getOferta() {
        return oferta;
    }

    public TipoDisciplinaMatricula getTipo() {
        return tipo;
    }
}
