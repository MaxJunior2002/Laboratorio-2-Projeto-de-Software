package com.projetosft.labdois.modules.disciplina.service;

import com.projetosft.labdois.modules.curso.repository.CursoRepository;
import com.projetosft.labdois.modules.disciplina.domain.Disciplina;
import com.projetosft.labdois.modules.disciplina.repository.DisciplinaRepository;
import com.projetosft.labdois.modules.professor.repository.ProfessorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;
    private final CursoRepository cursoRepository;
    private final ProfessorRepository professorRepository;

    public DisciplinaService(DisciplinaRepository disciplinaRepository,
                             CursoRepository cursoRepository,
                             ProfessorRepository professorRepository) {
        this.disciplinaRepository = disciplinaRepository;
        this.cursoRepository = cursoRepository;
        this.professorRepository = professorRepository;
    }

    public Disciplina criar(String nome, int cargaHoraria, UUID cursoId, UUID professorId) {
        if (nome == null || nome.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome da disciplina é obrigatório.");
        }

        var curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado."));

        var professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor não encontrado."));

        Disciplina disciplina = new Disciplina(nome.trim(), cargaHoraria, curso, professor);
        return disciplinaRepository.save(disciplina);
    }
}
