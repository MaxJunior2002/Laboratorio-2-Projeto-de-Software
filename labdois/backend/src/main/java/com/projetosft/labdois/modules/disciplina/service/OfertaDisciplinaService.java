package com.projetosft.labdois.modules.disciplina.service;

import com.projetosft.labdois.modules.disciplina.domain.Disciplina;
import com.projetosft.labdois.modules.disciplina.domain.OfertaDisciplina;
import com.projetosft.labdois.modules.disciplina.repository.DisciplinaRepository;
import com.projetosft.labdois.modules.disciplina.repository.OfertaDisciplinaRepository;
import com.projetosft.labdois.modules.matricula.domain.PeriodoInscricao;
import com.projetosft.labdois.modules.matricula.repository.PeriodoInscricaoRepository;
import com.projetosft.labdois.modules.professor.domain.Professor;
import com.projetosft.labdois.modules.professor.repository.ProfessorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class OfertaDisciplinaService {

    private final OfertaDisciplinaRepository ofertaRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final PeriodoInscricaoRepository periodoRepository;
    private final ProfessorRepository professorRepository;

    public OfertaDisciplinaService(
            OfertaDisciplinaRepository ofertaRepository,
            DisciplinaRepository disciplinaRepository,
            PeriodoInscricaoRepository periodoRepository,
            ProfessorRepository professorRepository) {
        this.ofertaRepository = ofertaRepository;
        this.disciplinaRepository = disciplinaRepository;
        this.periodoRepository = periodoRepository;
        this.professorRepository = professorRepository;
    }

    @Transactional
    public OfertaDisciplina criar(
            UUID disciplinaId, String periodo, UUID professorId, int capacidadeMaxima, int minimoAlunos) {
        if (periodo == null || periodo.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Período letivo é obrigatório.");
        }
        if (capacidadeMaxima < 1 || minimoAlunos < 1 || minimoAlunos > capacidadeMaxima) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Limites de alunos da oferta são inválidos.");
        }
        PeriodoInscricao periodoInscricao = periodoRepository.findByPeriodo(periodo.trim())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Período letivo não encontrado."));
        if (periodoInscricao.isEncerrado()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Não é possível criar oferta para um período encerrado.");
        }
        Disciplina disciplina = disciplinaRepository.findById(disciplinaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Disciplina não encontrada."));
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor não encontrado."));
        if (ofertaRepository.existsByDisciplina_IdAndPeriodoInscricao_Periodo(
                disciplinaId, periodoInscricao.getPeriodo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A disciplina já possui uma oferta nesse período.");
        }
        return ofertaRepository.save(new OfertaDisciplina(
                disciplina, periodoInscricao, professor, capacidadeMaxima, minimoAlunos));
    }

    @Transactional(readOnly = true)
    public List<OfertaDisciplina> listarPorPeriodo(String periodo) {
        return ofertaRepository.findByPeriodoInscricao_Periodo(validarPeriodo(periodo));
    }

    @Transactional(readOnly = true)
    public List<OfertaDisciplina> listarPorProfessor(UUID professorId, String periodo) {
        return ofertaRepository.findByProfessor_IdAndPeriodoInscricao_Periodo(
                professorId, validarPeriodo(periodo));
    }

    @Transactional(readOnly = true)
    public OfertaDisciplina buscar(UUID id) {
        return ofertaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Oferta não encontrada."));
    }

    private String validarPeriodo(String periodo) {
        if (periodo == null || periodo.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Período letivo é obrigatório.");
        }
        return periodo.trim();
    }
}
