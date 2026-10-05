package com.projetosft.labdois.modules.matricula.service;

import com.projetosft.labdois.modules.aluno.domain.Aluno;
import com.projetosft.labdois.modules.aluno.repository.AlunoRepository;
import com.projetosft.labdois.modules.disciplina.domain.Disciplina;
import com.projetosft.labdois.modules.disciplina.domain.StatusDisciplina;
import com.projetosft.labdois.modules.disciplina.repository.DisciplinaRepository;
import com.projetosft.labdois.modules.matricula.domain.Matricula;
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
    private final DisciplinaRepository disciplinaRepository;

    public MatriculaService(
            MatriculaRepository matriculaRepository,
            MatriculaDisciplinaRepository matriculaDisciplinaRepository,
            AlunoRepository alunoRepository,
            DisciplinaRepository disciplinaRepository) {
        this.matriculaRepository = matriculaRepository;
        this.matriculaDisciplinaRepository = matriculaDisciplinaRepository;
        this.alunoRepository = alunoRepository;
        this.disciplinaRepository = disciplinaRepository;
    }

    @Transactional
    public Matricula criar(CriarMatriculaRequest request) {
        List<UUID> obrigatorias = validarLista(
                request.getDisciplinasObrigatorias(), Matricula.MAX_OBRIGATORIAS, "obrigatórias");
        List<UUID> optativas = validarLista(
                request.getDisciplinasOptativas(), Matricula.MAX_OPTATIVAS, "optativas");
        if (obrigatorias.isEmpty() && optativas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A matrícula precisa conter ao menos uma disciplina.");
        }
        if (request.getPeriodo() == null || request.getPeriodo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Período letivo é obrigatório.");
        }
        if (new HashSet<>(obrigatorias).size() != obrigatorias.size()
                || new HashSet<>(optativas).size() != optativas.size()
                || !java.util.Collections.disjoint(obrigatorias, optativas)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Uma disciplina não pode ser selecionada mais de uma vez.");
        }

        Aluno aluno = alunoRepository.findById(request.getAlunoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado."));
        String periodo = request.getPeriodo().trim();
        if (matriculaRepository.findByAluno_IdAndPeriodo(aluno.getId(), periodo).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "O aluno já possui uma matrícula nesse período.");
        }

        List<UUID> idsOrdenados = new ArrayList<>();
        idsOrdenados.addAll(obrigatorias);
        idsOrdenados.addAll(optativas);
        idsOrdenados.sort(UUID::compareTo);
        Map<UUID, Disciplina> disciplinas = idsOrdenados.stream()
                .map(id -> disciplinaRepository.findLockedById(id)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Disciplina não encontrada: " + id)))
                .collect(Collectors.toMap(Disciplina::getId, Function.identity()));

        for (UUID disciplinaId : idsOrdenados) {
            Disciplina disciplina = disciplinas.get(disciplinaId);
            if (disciplina.getStatus() != StatusDisciplina.ABERTA) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "A disciplina não está recebendo matrículas: " + disciplina.getNome());
            }
            long matriculados = matriculaDisciplinaRepository
                    .countByDisciplina_IdAndMatricula_Periodo(disciplinaId, periodo);
            if (matriculados >= disciplina.getCapacidadeMaxima()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "A disciplina atingiu a capacidade máxima: " + disciplina.getNome());
            }
        }

        Matricula matricula = new Matricula(periodo, LocalDate.now(), aluno);
        obrigatorias.forEach(id -> matricula.adicionarDisciplina(
                disciplinas.get(id), TipoDisciplinaMatricula.OBRIGATORIA));
        optativas.forEach(id -> matricula.adicionarDisciplina(
                disciplinas.get(id), TipoDisciplinaMatricula.OPTATIVA));
        matricula.confirmar();

        Matricula salva = matriculaRepository.save(matricula);
        for (UUID disciplinaId : idsOrdenados) {
            Disciplina disciplina = disciplinas.get(disciplinaId);
            long matriculados = matriculaDisciplinaRepository
                    .countByDisciplina_IdAndMatricula_Periodo(disciplinaId, periodo);
            if (matriculados >= disciplina.getCapacidadeMaxima()) {
                disciplina.setStatus(StatusDisciplina.ENCERRADA);
            }
        }
        return salva;
    }

    public Matricula buscar(UUID id) {
        return matriculaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Matrícula não encontrada."));
    }

    private List<UUID> validarLista(List<UUID> ids, int limite, String categoria) {
        if (ids == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A lista de disciplinas " + categoria + " é obrigatória.");
        }
        if (ids.size() > limite) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O limite é de " + limite + " disciplinas " + categoria + ".");
        }
        if (ids.stream().anyMatch(id -> id == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Os identificadores das disciplinas são obrigatórios.");
        }
        return ids;
    }
}
