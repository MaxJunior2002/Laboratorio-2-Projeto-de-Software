package com.projetosft.labdois.modules.cobranca.service;

import com.projetosft.labdois.modules.cobranca.domain.NotificacaoCobranca;
import com.projetosft.labdois.modules.cobranca.dto.NotificacaoCobrancaResponse;
import com.projetosft.labdois.modules.cobranca.repository.NotificacaoCobrancaRepository;
import com.projetosft.labdois.modules.matricula.domain.Matricula;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SimuladorCobrancaService {

    private final NotificacaoCobrancaRepository notificacaoRepository;

    public SimuladorCobrancaService(NotificacaoCobrancaRepository notificacaoRepository) {
        this.notificacaoRepository = notificacaoRepository;
    }

    @Transactional
    public void solicitar(Matricula matricula) {
        notificacaoRepository.save(new NotificacaoCobranca(matricula, LocalDateTime.now()));
    }

    @Transactional
    public void cancelar(UUID matriculaId) {
        notificacaoRepository.findByMatricula_Id(matriculaId)
                .ifPresent(NotificacaoCobranca::cancelar);
    }

    @Transactional(readOnly = true)
    public NotificacaoCobrancaResponse buscarPorMatricula(UUID matriculaId) {
        NotificacaoCobranca notificacao = notificacaoRepository.findByMatricula_Id(matriculaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Notificação de cobrança não encontrada."));
        return NotificacaoCobrancaResponse.from(notificacao);
    }
}
