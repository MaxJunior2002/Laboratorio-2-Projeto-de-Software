package com.projetosft.labdois.modules.disciplina.service;

import com.projetosft.labdois.modules.curso.domain.Curso;
import com.projetosft.labdois.modules.curso.repository.CursoRepository;
import com.projetosft.labdois.modules.disciplina.domain.Disciplina;
import com.projetosft.labdois.modules.disciplina.repository.DisciplinaRepository;
import com.projetosft.labdois.modules.matricula.repository.MatriculaRepository;
import com.projetosft.labdois.modules.professor.domain.Professor;
import com.projetosft.labdois.modules.professor.repository.ProfessorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;
    private final MatriculaRepository matriculaRepository;
    private final CursoRepository cursoRepository;
    private final ProfessorRepository professorRepository;

    public DisciplinaService(DisciplinaRepository disciplinaRepository,
                             MatriculaRepository matriculaRepository,
                             CursoRepository cursoRepository,
                             ProfessorRepository professorRepository) {
        this.disciplinaRepository = disciplinaRepository;
        this.matriculaRepository = matriculaRepository;
        this.cursoRepository = cursoRepository;
        this.professorRepository = professorRepository;
    }

    public Disciplina criar(String nome, int cargaHoraria, UUID cursoId, UUID professorId) {
        String nomeNormalizado = validarNome(nome);
        validarCargaHoraria(cargaHoraria);
        var curso = buscarCurso(cursoId);
        var professor = buscarProfessor(professorId);

        Disciplina disciplina = new Disciplina(nomeNormalizado, cargaHoraria, curso, professor);
        return disciplinaRepository.save(disciplina);
    }

    public List<Disciplina> listar() {
        return disciplinaRepository.findAll();
    }

    public Disciplina buscar(UUID id) {
        return disciplinaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Disciplina não encontrada."));
    }

    public Disciplina atualizar(UUID id, String nome, int cargaHoraria, UUID cursoId, UUID professorId) {
        Disciplina disciplina = buscar(id);
        String nomeNormalizado = validarNome(nome);
        validarCargaHoraria(cargaHoraria);
        var curso = buscarCurso(cursoId);
        var professor = buscarProfessor(professorId);

        disciplina.setNome(nomeNormalizado);
        disciplina.setCargaHoraria(cargaHoraria);
        disciplina.setCurso(curso);
        disciplina.setProfessor(professor);
        return disciplinaRepository.save(disciplina);
    }

    public void excluir(UUID id) {
        Disciplina disciplina = buscar(id);
        if (matriculaRepository.existsByDisciplinas_Disciplina_Id(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Não é possível excluir uma disciplina vinculada a matrículas.");
        }
        disciplinaRepository.delete(disciplina);
    }

    private String validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome da disciplina é obrigatório.");
        }
        return nome.trim();
    }

    private void validarCargaHoraria(int cargaHoraria) {
        if (cargaHoraria < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Carga horária deve ser maior que zero.");
        }
    }

    private Curso buscarCurso(UUID cursoId) {
        if (cursoId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Curso é obrigatório.");
        }
        return cursoRepository.findById(cursoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado."));
    }

    private Professor buscarProfessor(UUID professorId) {
        if (professorId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Professor é obrigatório.");
        }
        return professorRepository.findById(professorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor não encontrado."));
    }
}
