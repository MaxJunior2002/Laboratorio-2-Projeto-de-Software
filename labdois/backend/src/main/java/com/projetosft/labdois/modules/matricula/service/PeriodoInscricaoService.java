package com.projetosft.labdois.modules.matricula.service;

import com.projetosft.labdois.modules.disciplina.domain.Disciplina;
import com.projetosft.labdois.modules.disciplina.domain.StatusDisciplina;
import com.projetosft.labdois.modules.disciplina.repository.DisciplinaRepository;
import com.projetosft.labdois.modules.matricula.domain.PeriodoInscricao;
import com.projetosft.labdois.modules.matricula.domain.StatusMatricula;
import com.projetosft.labdois.modules.matricula.repository.MatriculaDisciplinaRepository;
import com.projetosft.labdois.modules.matricula.repository.PeriodoInscricaoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class PeriodoInscricaoService {

    private final PeriodoInscricaoRepository periodoInscricaoRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final MatriculaDisciplinaRepository matriculaDisciplinaRepository;

    public PeriodoInscricaoService(
            PeriodoInscricaoRepository periodoInscricaoRepository,
            DisciplinaRepository disciplinaRepository,
            MatriculaDisciplinaRepository matriculaDisciplinaRepository) {
        this.periodoInscricaoRepository = periodoInscricaoRepository;
        this.disciplinaRepository = disciplinaRepository;
        this.matriculaDisciplinaRepository = matriculaDisciplinaRepository;
    }

    @Transactional
    public PeriodoInscricao criar(String periodo, LocalDate inicio, LocalDate fim) {
        String nomePeriodo = validar(periodo, inicio, fim);
        if (periodoInscricaoRepository.findByPeriodo(nomePeriodo).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Período letivo já cadastrado.");
        }
        return periodoInscricaoRepository.save(new PeriodoInscricao(nomePeriodo, inicio, fim));
    }

    @Transactional
    public PeriodoInscricao atualizar(String periodo, LocalDate inicio, LocalDate fim) {
        String nomePeriodo = validar(periodo, inicio, fim);
        PeriodoInscricao existente = periodoInscricaoRepository.findByPeriodo(nomePeriodo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Período letivo não encontrado."));
        if (existente.isEncerrado()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Não é possível alterar um período de inscrição já encerrado.");
        }
        existente.atualizarDatas(inicio, fim);
        return periodoInscricaoRepository.save(existente);
    }

    @Transactional(readOnly = true)
    public List<PeriodoInscricao> listar() {
        return periodoInscricaoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PeriodoInscricao buscar(String periodo) {
        String nomePeriodo = validarNomePeriodo(periodo);
        return periodoInscricaoRepository.findByPeriodo(nomePeriodo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Período letivo não encontrado."));
    }

    @Transactional
    public void validarAberto(String periodo, LocalDate data) {
        PeriodoInscricao periodoInscricao = buscarBloqueado(periodo);
        if (!periodoInscricao.estaAbertoEm(data)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "O período de inscrições está fechado para " + periodoInscricao.getPeriodo() + ".");
        }
    }

    @Transactional
    public ResumoEncerramento encerrar(String periodo, LocalDate data) {
        PeriodoInscricao periodoInscricao = buscarBloqueado(periodo);
        if (periodoInscricao.isEncerrado()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "O período de inscrição já foi encerrado.");
        }
        if (!data.isAfter(periodoInscricao.getFim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "O período só pode ser encerrado após a data final das inscrições.");
        }

        int disciplinasAtivadas = 0;
        int disciplinasCanceladas = 0;
        for (Disciplina disciplina : disciplinaRepository.findAllLocked()) {
            long alunosInscritos = matriculaDisciplinaRepository.contarAlunosAtivosPorDisciplinaEPeriodo(
                    disciplina.getId(), periodoInscricao.getPeriodo(), StatusMatricula.ATIVA);
            if (alunosInscritos >= disciplina.getMinimoAlunos()) {
                disciplina.setStatus(StatusDisciplina.ATIVA);
                disciplinasAtivadas++;
            } else {
                disciplina.setStatus(StatusDisciplina.CANCELADA);
                disciplinasCanceladas++;
            }
        }
        periodoInscricao.encerrar(data);
        return new ResumoEncerramento(periodoInscricao.getPeriodo(), disciplinasAtivadas, disciplinasCanceladas);
    }

    private PeriodoInscricao buscarBloqueado(String periodo) {
        String nomePeriodo = validarNomePeriodo(periodo);
        return periodoInscricaoRepository.findLockedByPeriodo(nomePeriodo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Período letivo não encontrado."));
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

    public record ResumoEncerramento(String periodo, int disciplinasAtivadas, int disciplinasCanceladas) {
    }
}
