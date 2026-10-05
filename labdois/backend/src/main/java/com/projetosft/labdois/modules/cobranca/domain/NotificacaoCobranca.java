package com.projetosft.labdois.modules.cobranca.domain;

import com.projetosft.labdois.modules.matricula.domain.Matricula;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notificacoes_cobranca", uniqueConstraints = {
        @UniqueConstraint(columnNames = "matricula_id")
})
public class NotificacaoCobranca {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @Column(nullable = false)
    private LocalDateTime solicitadaEm;

    @Column(nullable = false)
    private int quantidadeDisciplinas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCobranca status = StatusCobranca.SOLICITADA;

    protected NotificacaoCobranca() {
    }

    public NotificacaoCobranca(Matricula matricula, LocalDateTime solicitadaEm) {
        this.matricula = matricula;
        this.solicitadaEm = solicitadaEm;
        this.quantidadeDisciplinas = matricula.getDisciplinas().size();
    }

    public void cancelar() {
        status = StatusCobranca.CANCELADA;
    }

    public UUID getId() {
        return id;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public LocalDateTime getSolicitadaEm() {
        return solicitadaEm;
    }

    public int getQuantidadeDisciplinas() {
        return quantidadeDisciplinas;
    }

    public StatusCobranca getStatus() {
        return status;
    }
}
