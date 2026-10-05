package com.projetosft.labdois.modules;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.projetosft.labdois.modules.curso.domain.Curso;
import com.projetosft.labdois.modules.curso.repository.CursoRepository;
import com.projetosft.labdois.modules.disciplina.domain.Disciplina;
import com.projetosft.labdois.modules.disciplina.repository.DisciplinaRepository;
import com.projetosft.labdois.modules.professor.domain.Professor;
import com.projetosft.labdois.modules.professor.repository.ProfessorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CursoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = JsonMapper.builder().build();

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @BeforeEach
    void limparDados() {
        disciplinaRepository.deleteAll();
        cursoRepository.deleteAll();
        professorRepository.deleteAll();
    }

    @Test
    void deveCriarListarEBuscarCurso() throws Exception {
        String id = criarCurso("Engenharia de Software", 40);

        mockMvc.perform(get("/api/cursos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].nome").value("Engenharia de Software"))
                .andExpect(jsonPath("$[0].numeroCreditos").value(40));

        mockMvc.perform(get("/api/cursos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome").value("Engenharia de Software"));
    }

    @Test
    void deveAtualizarCurso() throws Exception {
        String id = criarCurso("Engenharia de Software", 40);

        mockMvc.perform(put("/api/cursos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Ciência da Computação","numeroCreditos":36}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Ciência da Computação"))
                .andExpect(jsonPath("$.numeroCreditos").value(36));
    }

    @Test
    void deveExcluirCursoSemDisciplinasVinculadas() throws Exception {
        String id = criarCurso("Engenharia de Software", 40);

        mockMvc.perform(delete("/api/cursos/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/cursos/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void naoDeveExcluirCursoComDisciplinasVinculadas() throws Exception {
        Curso curso = cursoRepository.save(new Curso("Engenharia de Software", 40));
        Professor professor = professorRepository.save(
                new Professor("Carlos Lima", "carlos@example.com", "senha", "P-1001"));
        disciplinaRepository.save(new Disciplina("Programação", 60, curso, professor));

        mockMvc.perform(delete("/api/cursos/{id}", curso.getId()))
                .andExpect(status().isConflict());
    }

    @Test
    void deveValidarDadosObrigatoriosEDuplicidade() throws Exception {
        criarCurso("Engenharia de Software", 40);

        mockMvc.perform(post("/api/cursos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Engenharia de Software","numeroCreditos":40}
                                """))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/cursos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Curso inválido"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornarNaoEncontradoParaCursoInexistente() throws Exception {
        mockMvc.perform(get("/api/cursos/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    private String criarCurso(String nome, int creditos) throws Exception {
        String response = mockMvc.perform(post("/api/cursos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"%s","numeroCreditos":%d}
                                """.formatted(nome, creditos)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value(nome))
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).get("id").asText();
    }
}
