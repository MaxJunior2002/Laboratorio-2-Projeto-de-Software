package com.projetosft.labdois.modules.curso.service;

import com.projetosft.labdois.modules.curso.domain.Curso;
import com.projetosft.labdois.modules.curso.repository.CursoRepository;
import com.projetosft.labdois.modules.disciplina.repository.DisciplinaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;
    private final DisciplinaRepository disciplinaRepository;

    public CursoService(CursoRepository cursoRepository, DisciplinaRepository disciplinaRepository) {
        this.cursoRepository = cursoRepository;
        this.disciplinaRepository = disciplinaRepository;
    }

    public Curso criar(String nome, int numeroCreditos) {
        String nomeNormalizado = validarNome(nome);
        if (cursoRepository.findByNome(nomeNormalizado).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Curso já cadastrado.");
        }
        validarCreditos(numeroCreditos);
        return cursoRepository.save(new Curso(nomeNormalizado, numeroCreditos));
    }

    public List<Curso> listar() {
        return cursoRepository.findAll();
    }

    public Curso buscar(UUID id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado."));
    }

    public Curso atualizar(UUID id, String nome, int numeroCreditos) {
        Curso curso = buscar(id);
        String nomeNormalizado = validarNome(nome);
        if (cursoRepository.findByNomeAndIdNot(nomeNormalizado, id).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Curso já cadastrado.");
        }
        validarCreditos(numeroCreditos);
        curso.setNome(nomeNormalizado);
        curso.setNumeroCreditos(numeroCreditos);
        return cursoRepository.save(curso);
    }

    public void excluir(UUID id) {
        Curso curso = buscar(id);
        if (disciplinaRepository.existsByCurso_Id(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Não é possível excluir um curso que possui disciplinas vinculadas.");
        }
        cursoRepository.delete(curso);
    }

    private String validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome do curso é obrigatório.");
        }
        return nome.trim();
    }

    private void validarCreditos(int numeroCreditos) {
        if (numeroCreditos < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Número de créditos não pode ser negativo.");
        }
    }
}
