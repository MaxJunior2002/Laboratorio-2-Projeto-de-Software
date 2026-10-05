package com.projetosft.labdois.modules.matricula.service;

import com.projetosft.labdois.modules.matricula.domain.PeriodoInscricao;
import com.projetosft.labdois.modules.matricula.repository.PeriodoInscricaoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class PeriodoInscricaoService {

    private final PeriodoInscricaoRepository periodoInscricaoRepository;

    public PeriodoInscricaoService(PeriodoInscricaoRepository periodoInscricaoRepository) {
        this.periodoInscricaoRepository = periodoInscricaoRepository;
    }

    public PeriodoInscricao criar(String periodo, LocalDate inicio, LocalDate fim) {
        String nomePeriodo = validar(periodo, inicio, fim);
        if (periodoInscricaoRepository.findByPeriodo(nomePeriodo).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Período letivo já cadastrado.");
        }
        return periodoInscricaoRepository.save(new PeriodoInscricao(nomePeriodo, inicio, fim));
    }

    public PeriodoInscricao atualizar(String periodo, LocalDate inicio, LocalDate fim) {
        String nomePeriodo = validar(periodo, inicio, fim);
        PeriodoInscricao existente = periodoInscricaoRepository.findByPeriodo(nomePeriodo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Período letivo não encontrado."));
        existente.atualizarDatas(inicio, fim);
        return periodoInscricaoRepository.save(existente);
    }

    public List<PeriodoInscricao> listar() {
        return periodoInscricaoRepository.findAll();
    }

    public PeriodoInscricao buscar(String periodo) {
        String nomePeriodo = validarNomePeriodo(periodo);
        return periodoInscricaoRepository.findByPeriodo(nomePeriodo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Período letivo não encontrado."));
    }

    public void validarAberto(String periodo, LocalDate data) {
        PeriodoInscricao periodoInscricao = buscar(periodo);
        if (!periodoInscricao.estaAbertoEm(data)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "O período de inscrições está fechado para " + periodoInscricao.getPeriodo() + ".");
        }
    }

    private String validar(String periodo, LocalDate inicio, LocalDate fim) {
        String nomePeriodo = validarNomePeriodo(periodo);
        if (inicio == null || fim == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "As datas de início e fim são obrigatórias.");
        }
        if (fim.isBefore(inicio)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A data de fim não pode ser anterior à data de início.");
        }
        return nomePeriodo;
    }

    private String validarNomePeriodo(String periodo) {
        if (periodo == null || periodo.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Período letivo é obrigatório.");
        }
        return periodo.trim();
    }
}
