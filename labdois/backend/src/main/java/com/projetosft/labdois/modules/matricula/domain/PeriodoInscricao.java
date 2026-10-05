package com.projetosft.labdois.modules.matricula.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "periodos_inscricao")
public class PeriodoInscricao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String periodo;

    @Column(nullable = false)
    private LocalDate inicio;

    @Column(nullable = false)
    private LocalDate fim;

    @Column(nullable = false)
    private boolean encerrado;

    protected PeriodoInscricao() {
    }

    public PeriodoInscricao(String periodo, LocalDate inicio, LocalDate fim) {
        this.periodo = periodo;
        this.inicio = inicio;
        this.fim = fim;
    }

    public UUID getId() {
        return id;
    }

    public String getPeriodo() {
        return periodo;
    }

    public LocalDate getInicio() {
        return inicio;
    }

    public LocalDate getFim() {
        return fim;
    }

    public boolean isEncerrado() {
        return encerrado;
    }

    public void atualizarDatas(LocalDate inicio, LocalDate fim) {
        if (encerrado) {
            throw new IllegalStateException("O período de inscrição já foi encerrado.");
        }
        this.inicio = inicio;
        this.fim = fim;
    }

    public void encerrar(LocalDate data) {
        if (encerrado) {
            throw new IllegalStateException("O período de inscrição já foi encerrado.");
        }
        if (!data.isAfter(fim)) {
            throw new IllegalStateException("O período só pode ser encerrado após a data final.");
        }
        encerrado = true;
    }

    public boolean estaAbertoEm(LocalDate data) {
        return !encerrado && !data.isBefore(inicio) && !data.isAfter(fim);
    }
}
