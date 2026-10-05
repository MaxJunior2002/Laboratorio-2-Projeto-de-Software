package com.projetosft.labdois.modules.cobranca.repository;

import com.projetosft.labdois.modules.cobranca.domain.NotificacaoCobranca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NotificacaoCobrancaRepository extends JpaRepository<NotificacaoCobranca, UUID> {

    Optional<NotificacaoCobranca> findByMatricula_Id(UUID matriculaId);
}
