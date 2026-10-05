package com.projetosft.labdois.modules;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.projetosft.labdois.modules.aluno.domain.Aluno;
import com.projetosft.labdois.modules.aluno.repository.AlunoRepository;
import com.projetosft.labdois.modules.curso.domain.Curso;
import com.projetosft.labdois.modules.curso.repository.CursoRepository;
import com.projetosft.labdois.modules.disciplina.domain.Disciplina;
import com.projetosft.labdois.modules.disciplina.repository.DisciplinaRepository;
import com.projetosft.labdois.modules.matricula.domain.Matricula;
import com.projetosft.labdois.modules.matricula.repository.MatriculaRepository;
import com.projetosft.labdois.modules.professor.domain.Professor;
import com.projetosft.labdois.modules.professor.repository.ProfessorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DisciplinaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private MatriculaRepository matriculaRepository;

    private final ObjectMapper objectMapper = JsonMapper.builder().build();

    @BeforeEach
    void limparDados() {
        matriculaRepository.deleteAll();
        disciplinaRepository.deleteAll();
        alunoRepository.deleteAll();
        professorRepository.deleteAll();
        cursoRepository.deleteAll();
    }

    @Test
    void deveCriarListarEBuscarDisciplina() throws Exception {
        Curso curso = criarCurso("Engenharia de Software");
        Professor professor = criarProfessor("Ana Souza", "ana@example.com");
        String id = criarDisciplina("Programação", 60, curso.getId(), professor.getId());

        mockMvc.perform(get("/api/disciplinas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].nome").value("Programação"))
                .andExpect(jsonPath("$[0].cursoNome").value("Engenharia de Software"))
                .andExpect(jsonPath("$[0].professorNome").value("Ana Souza"))
                .andExpect(jsonPath("$[0].capacidadeMaxima").value(60))
                .andExpect(jsonPath("$[0].status").value("ABERTA"));

        mockMvc.perform(get("/api/disciplinas/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome").value("Programação"));
    }

    @Test
    void deveAtualizarDisciplinaEAssociacoes() throws Exception {
        Curso curso = criarCurso("Engenharia de Software");
        Curso novoCurso = criarCurso("Ciência da Computação");
        Professor professor = criarProfessor("Ana Souza", "ana@example.com");
        Professor novoProfessor = criarProfessor("Bruno Lima", "bruno@example.com");
        String id = criarDisciplina("Programação", 60, curso.getId(), professor.getId());

        mockMvc.perform(put("/api/disciplinas/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson("Estruturas de Dados", 80, novoCurso.getId(), novoProfessor.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Estruturas de Dados"))
                .andExpect(jsonPath("$.cargaHoraria").value(80))
                .andExpect(jsonPath("$.cursoId").value(novoCurso.getId().toString()))
                .andExpect(jsonPath("$.professorId").value(novoProfessor.getId().toString()));
    }

    @Test
    void deveExcluirDisciplinaSemMatriculas() throws Exception {
        Curso curso = criarCurso("Engenharia de Software");
        Professor professor = criarProfessor("Ana Souza", "ana@example.com");
        String id = criarDisciplina("Programação", 60, curso.getId(), professor.getId());

        mockMvc.perform(delete("/api/disciplinas/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/disciplinas/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void naoDeveExcluirDisciplinaVinculadaAMatricula() throws Exception {
        Curso curso = criarCurso("Engenharia de Software");
        Professor professor = criarProfessor("Ana Souza", "ana@example.com");
        Aluno aluno = alunoRepository.save(new Aluno("Caio Silva", "caio@example.com", "senha", "2025001"));
        Disciplina disciplina = disciplinaRepository.save(new Disciplina("Programação", 60, curso, professor));
        Matricula matricula = new Matricula("2026.1", LocalDate.now(), aluno);
        matricula.getDisciplinas().add(disciplina);
        matriculaRepository.save(matricula);

        mockMvc.perform(delete("/api/disciplinas/{id}", disciplina.getId()))
                .andExpect(status().isConflict());
    }

    @Test
    void deveValidarDadosEReferenciasAoCriarDisciplina() throws Exception {
        Curso curso = criarCurso("Engenharia de Software");
        Professor professor = criarProfessor("Ana Souza", "ana@example.com");

        mockMvc.perform(post("/api/disciplinas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson("", 0, curso.getId(), professor.getId())))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/disciplinas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson("Programação", 60, UUID.randomUUID(), professor.getId())))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornarNaoEncontradoParaDisciplinaInexistente() throws Exception {
        mockMvc.perform(get("/api/disciplinas/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    private Curso criarCurso(String nome) {
        return cursoRepository.save(new Curso(nome, 40));
    }

    private Professor criarProfessor(String nome, String email) {
        return professorRepository.save(new Professor(nome, email, "senha", UUID.randomUUID().toString()));
    }

    private String criarDisciplina(String nome, int cargaHoraria, UUID cursoId, UUID professorId) throws Exception {
        String response = mockMvc.perform(post("/api/disciplinas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson(nome, cargaHoraria, cursoId, professorId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value(nome))
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).get("id").asText();
    }

    private String requestJson(String nome, int cargaHoraria, UUID cursoId, UUID professorId) {
        return """
                {"nome":"%s","cargaHoraria":%d,"cursoId":"%s","professorId":"%s"}
                """.formatted(nome, cargaHoraria, cursoId, professorId);
    }
}
