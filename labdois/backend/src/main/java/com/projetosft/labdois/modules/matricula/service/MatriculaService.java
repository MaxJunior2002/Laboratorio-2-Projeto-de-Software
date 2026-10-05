package com.projetosft.labdois.modules.matricula.service;

import com.projetosft.labdois.modules.aluno.domain.Aluno;
import com.projetosft.labdois.modules.aluno.repository.AlunoRepository;
import com.projetosft.labdois.modules.cobranca.service.SimuladorCobrancaService;
import com.projetosft.labdois.modules.disciplina.domain.OfertaDisciplina;
import com.projetosft.labdois.modules.disciplina.domain.StatusDisciplina;
import com.projetosft.labdois.modules.disciplina.repository.OfertaDisciplinaRepository;
import com.projetosft.labdois.modules.matricula.domain.Matricula;
import com.projetosft.labdois.modules.matricula.domain.StatusMatricula;
import com.projetosft.labdois.modules.matricula.domain.TipoDisciplinaMatricula;
import com.projetosft.labdois.modules.matricula.dto.CriarMatriculaRequest;
import com.projetosft.labdois.modules.matricula.repository.MatriculaDisciplinaRepository;
import com.projetosft.labdois.modules.matricula.repository.MatriculaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final MatriculaDisciplinaRepository matriculaDisciplinaRepository;
    private final AlunoRepository alunoRepository;
    private final OfertaDisciplinaRepository ofertaRepository;
    private final PeriodoInscricaoService periodoInscricaoService;
    private final SimuladorCobrancaService simuladorCobrancaService;

    public MatriculaService(
            MatriculaRepository matriculaRepository,
            MatriculaDisciplinaRepository matriculaDisciplinaRepository,
            AlunoRepository alunoRepository,
            OfertaDisciplinaRepository ofertaRepository,
            PeriodoInscricaoService periodoInscricaoService,
            SimuladorCobrancaService simuladorCobrancaService) {
        this.matriculaRepository = matriculaRepository;
        this.matriculaDisciplinaRepository = matriculaDisciplinaRepository;
        this.alunoRepository = alunoRepository;
        this.ofertaRepository = ofertaRepository;
        this.periodoInscricaoService = periodoInscricaoService;
        this.simuladorCobrancaService = simuladorCobrancaService;
    }

    @Transactional
    public Matricula criar(CriarMatriculaRequest request) {
        List<UUID> obrigatorias = validarLista(
                request.getOfertasObrigatorias(), Matricula.MAX_OBRIGATORIAS, "obrigatórias");
        List<UUID> optativas = validarLista(
                request.getOfertasOptativas(), Matricula.MAX_OPTATIVAS, "optativas");
        if (obrigatorias.isEmpty() && optativas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A matrícula precisa conter ao menos uma oferta de disciplina.");
        }
        if (request.getPeriodo() == null || request.getPeriodo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Período letivo é obrigatório.");
        }
        if (new HashSet<>(obrigatorias).size() != obrigatorias.size()
                || new HashSet<>(optativas).size() != optativas.size()
                || !Collections.disjoint(obrigatorias, optativas)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Uma oferta não pode ser selecionada mais de uma vez.");
        }

        Aluno aluno = alunoRepository.findById(request.getAlunoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado."));
        String periodo = request.getPeriodo().trim();
        periodoInscricaoService.validarAberto(periodo, LocalDate.now());
        if (matriculaRepository.findByAluno_IdAndPeriodo(aluno.getId(), periodo).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "O aluno já possui uma matrícula nesse período.");
        }

        List<UUID> ofertaIds = new ArrayList<>();
        ofertaIds.addAll(obrigatorias);
        ofertaIds.addAll(optativas);
        ofertaIds.sort(UUID::compareTo);
        Map<UUID, OfertaDisciplina> ofertas = ofertaIds.stream()
                .map(id -> ofertaRepository.findLockedById(id)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Oferta de disciplina não encontrada: " + id)))
                .collect(Collectors.toMap(OfertaDisciplina::getId, Function.identity()));

        for (UUID ofertaId : ofertaIds) {
            OfertaDisciplina oferta = ofertas.get(ofertaId);
            if (!oferta.getPeriodoInscricao().getPeriodo().equals(periodo)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "A oferta não pertence ao período letivo informado.");
            }
            if (oferta.getStatus() != StatusDisciplina.ABERTA
                    && oferta.getStatus() != StatusDisciplina.ATIVA) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "A oferta não está recebendo matrículas: " + oferta.getDisciplina().getNome());
            }
            long matriculados = matriculaDisciplinaRepository
                    .countByOferta_IdAndMatricula_Status(ofertaId, StatusMatricula.ATIVA);
            if (matriculados >= oferta.getCapacidadeMaxima()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "A oferta atingiu a capacidade máxima: " + oferta.getDisciplina().getNome());
            }
        }

        Matricula matricula = new Matricula(periodo, LocalDate.now(), aluno);
        obrigatorias.forEach(id -> matricula.adicionarDisciplina(
                ofertas.get(id), TipoDisciplinaMatricula.OBRIGATORIA));
        optativas.forEach(id -> matricula.adicionarDisciplina(
                ofertas.get(id), TipoDisciplinaMatricula.OPTATIVA));
        matricula.confirmar();

        Matricula salva = matriculaRepository.save(matricula);
        simuladorCobrancaService.solicitar(salva);
        for (UUID ofertaId : ofertaIds) {
            OfertaDisciplina oferta = ofertas.get(ofertaId);
            long matriculados = matriculaDisciplinaRepository
                    .countByOferta_IdAndMatricula_Status(ofertaId, StatusMatricula.ATIVA);
            if (matriculados >= oferta.getCapacidadeMaxima()) {
                oferta.setStatus(StatusDisciplina.ENCERRADA);
            }
        }
        return salva;
    }

    @Transactional(readOnly = true)
    public Matricula buscar(UUID id) {
        return matriculaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Matrícula não encontrada."));
    }

    @Transactional
    public void cancelar(UUID id) {
        Matricula matricula = buscar(id);
        if (matricula.getStatus() == StatusMatricula.CANCELADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A matrícula já foi cancelada.");
        }
        periodoInscricaoService.validarAberto(matricula.getPeriodo(), LocalDate.now());
        List<UUID> ofertaIds = matricula.getDisciplinas().stream()
                .map(item -> item.getOferta().getId())
                .sorted()
                .toList();
        matricula.cancelar();
        matriculaRepository.save(matricula);
        simuladorCobrancaService.cancelar(matricula.getId());
        for (UUID ofertaId : ofertaIds) {
            OfertaDisciplina oferta = ofertaRepository.findLockedById(ofertaId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Oferta de disciplina não encontrada: " + ofertaId));
            long matriculados = matriculaDisciplinaRepository
                    .countByOferta_IdAndMatricula_Status(ofertaId, StatusMatricula.ATIVA);
            if (oferta.getStatus() == StatusDisciplina.ENCERRADA
                    && matriculados < oferta.getCapacidadeMaxima()) {
                oferta.setStatus(StatusDisciplina.ABERTA);
            }
        }
    }

    private List<UUID> validarLista(List<UUID> ids, int limite, String categoria) {
        if (ids == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A lista de ofertas " + categoria + " é obrigatória.");
        }
        if (ids.size() > limite) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O limite é de " + limite + " ofertas " + categoria + ".");
        }
        if (ids.stream().anyMatch(id -> id == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Os identificadores das ofertas são obrigatórios.");
        }
        return ids;
    }
}
