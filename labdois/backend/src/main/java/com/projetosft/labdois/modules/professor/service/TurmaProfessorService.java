package com.projetosft.labdois.modules.professor.service;

import com.projetosft.labdois.modules.disciplina.domain.OfertaDisciplina;
import com.projetosft.labdois.modules.disciplina.repository.OfertaDisciplinaRepository;
import com.projetosft.labdois.modules.matricula.domain.StatusMatricula;
import com.projetosft.labdois.modules.matricula.repository.MatriculaDisciplinaRepository;
import com.projetosft.labdois.modules.professor.dto.TurmaProfessorResponse;
import com.projetosft.labdois.modules.professor.repository.ProfessorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TurmaProfessorService {

    private final ProfessorRepository professorRepository;
    private final OfertaDisciplinaRepository ofertaRepository;
    private final MatriculaDisciplinaRepository matriculaDisciplinaRepository;

    public TurmaProfessorService(
            ProfessorRepository professorRepository,
            OfertaDisciplinaRepository ofertaRepository,
            MatriculaDisciplinaRepository matriculaDisciplinaRepository) {
        this.professorRepository = professorRepository;
        this.ofertaRepository = ofertaRepository;
        this.matriculaDisciplinaRepository = matriculaDisciplinaRepository;
    }

    @Transactional(readOnly = true)
    public List<TurmaProfessorResponse> listarTurmas(UUID professorId, String periodo) {
        if (!professorRepository.existsById(professorId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor não encontrado.");
        }
        if (periodo == null || periodo.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Período letivo é obrigatório.");
        }
        List<OfertaDisciplina> ofertas =
                ofertaRepository.findByProfessor_IdAndPeriodoInscricao_Periodo(professorId, periodo.trim());
        List<com.projetosft.labdois.modules.matricula.domain.MatriculaDisciplina> matriculas =
                matriculaDisciplinaRepository
                        .findByOferta_Professor_IdAndOferta_PeriodoInscricao_PeriodoAndMatricula_Status(
                                professorId, periodo.trim(), StatusMatricula.ATIVA);
        var porOferta = matriculas.stream().collect(Collectors.groupingBy(item -> item.getOferta().getId()));
        return ofertas.stream()
                .map(oferta -> TurmaProfessorResponse.from(
                        oferta, porOferta.getOrDefault(oferta.getId(), List.of())))
                .toList();
    }
}
