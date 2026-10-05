package com.projetosft.labdois.modules;

import com.projetosft.labdois.modules.aluno.domain.Aluno;
import com.projetosft.labdois.modules.aluno.repository.AlunoRepository;
import com.projetosft.labdois.modules.curso.domain.Curso;
import com.projetosft.labdois.modules.curso.repository.CursoRepository;
import com.projetosft.labdois.modules.disciplina.domain.Disciplina;
import com.projetosft.labdois.modules.disciplina.domain.StatusDisciplina;
import com.projetosft.labdois.modules.disciplina.repository.DisciplinaRepository;
import com.projetosft.labdois.modules.matricula.repository.MatriculaDisciplinaRepository;
import com.projetosft.labdois.modules.matricula.repository.MatriculaRepository;
import com.projetosft.labdois.modules.matricula.domain.PeriodoInscricao;
import com.projetosft.labdois.modules.matricula.repository.PeriodoInscricaoRepository;
import com.projetosft.labdois.modules.professor.domain.Professor;
import com.projetosft.labdois.modules.professor.repository.ProfessorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MatriculaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private MatriculaRepository matriculaRepository;

    @Autowired
    private MatriculaDisciplinaRepository matriculaDisciplinaRepository;

    @Autowired
    private PeriodoInscricaoRepository periodoInscricaoRepository;

    private Aluno aluno;
    private Disciplina disciplina1;
    private Disciplina disciplina2;

    @BeforeEach
    void prepararDados() {
        matriculaRepository.deleteAll();
        matriculaDisciplinaRepository.deleteAll();
        periodoInscricaoRepository.deleteAll();
        disciplinaRepository.deleteAll();
        alunoRepository.deleteAll();
        professorRepository.deleteAll();
        cursoRepository.deleteAll();

        aluno = alunoRepository.save(new Aluno("Ana Souza", "ana@example.com", "senha", "2026001"));
        Curso curso = cursoRepository.save(new Curso("Engenharia de Software", 40));
        Professor professor = professorRepository.save(
                new Professor("Carlos Lima", "carlos@example.com", "senha", "P-1001"));
        disciplina1 = disciplinaRepository.save(new Disciplina("Programação", 60, curso, professor));
        disciplina2 = disciplinaRepository.save(new Disciplina("Banco de Dados", 60, curso, professor));
        periodoInscricaoRepository.save(new PeriodoInscricao(
                "2026.1", LocalDate.now().minusDays(1), LocalDate.now().plusDays(1)));
    }

    @Test
    void deveCriarEConsultarMatriculaComCategorias() throws Exception {
        String response = mockMvc.perform(post("/api/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson(
                                aluno.getId(),
                                List.of(disciplina1.getId()),
                                List.of(disciplina2.getId()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.alunoId").value(aluno.getId().toString()))
                .andExpect(jsonPath("$.totalObrigatorias").value(1))
                .andExpect(jsonPath("$.totalOptativas").value(1))
                .andExpect(jsonPath("$.disciplinas[0].tipo").value("OBRIGATORIA"))
                .andExpect(jsonPath("$.disciplinas[1].tipo").value("OPTATIVA"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = com.fasterxml.jackson.databind.json.JsonMapper.builder()
                .build().readTree(response).get("id").asText();
        mockMvc.perform(get("/api/matriculas/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disciplinas.length()").value(2));
    }

    @Test
    void deveRejeitarMaisDeQuatroDisciplinasObrigatorias() throws Exception {
        mockMvc.perform(post("/api/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson(aluno.getId(),
                                List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                                        UUID.randomUUID(), UUID.randomUUID()),
                                List.of())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRejeitarMaisDeDuasDisciplinasOptativas() throws Exception {
        mockMvc.perform(post("/api/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson(aluno.getId(),
                                List.of(),
                                List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRejeitarDisciplinaDuplicadaEntreCategorias() throws Exception {
        mockMvc.perform(post("/api/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson(aluno.getId(),
                                List.of(disciplina1.getId()),
                                List.of(disciplina1.getId()))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveImpedirNovaMatriculaDoAlunoNoMesmoPeriodo() throws Exception {
        String body = requestJson(aluno.getId(), List.of(disciplina1.getId()), List.of());
        mockMvc.perform(post("/api/matriculas").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/matriculas").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void deveEncerrarDisciplinaAoAtingirCapacidade() throws Exception {
        disciplina1.setCapacidadeMaxima(1);
        disciplinaRepository.save(disciplina1);

        mockMvc.perform(post("/api/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson(aluno.getId(), List.of(disciplina1.getId()), List.of())))
                .andExpect(status().isCreated());

        org.junit.jupiter.api.Assertions.assertEquals(StatusDisciplina.ENCERRADA,
                disciplinaRepository.findById(disciplina1.getId()).orElseThrow().getStatus());

        Aluno segundoAluno = alunoRepository.save(
                new Aluno("Bruno Lima", "bruno@example.com", "senha", "2026002"));
        mockMvc.perform(post("/api/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson(segundoAluno.getId(), List.of(disciplina1.getId()), List.of())))
                .andExpect(status().isConflict());
    }

    @Test
    void deveCancelarMatriculaDuranteJanelaEConsultarEstado() throws Exception {
        String body = requestJson(aluno.getId(), List.of(disciplina1.getId()), List.of());
        String response = mockMvc.perform(post("/api/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String id = com.fasterxml.jackson.databind.json.JsonMapper.builder()
                .build().readTree(response).get("id").asText();

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .delete("/api/matriculas/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/matriculas/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADA"))
                .andExpect(jsonPath("$.dataCancelamento").isNotEmpty())
                .andExpect(jsonPath("$.disciplinas").isEmpty());
    }

    @Test
    void naoDeveCancelarMatriculaForaDaJanelaDeInscricao() throws Exception {
        String response = mockMvc.perform(post("/api/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson(aluno.getId(), List.of(disciplina1.getId()), List.of())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String id = com.fasterxml.jackson.databind.json.JsonMapper.builder()
                .build().readTree(response).get("id").asText();

        PeriodoInscricao periodo = periodoInscricaoRepository.findByPeriodo("2026.1").orElseThrow();
        periodo.atualizarDatas(LocalDate.now().minusDays(5), LocalDate.now().minusDays(1));
        periodoInscricaoRepository.save(periodo);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .delete("/api/matriculas/{id}", id))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRejeitarNovaMatriculaForaDaJanelaDeInscricao() throws Exception {
        PeriodoInscricao periodo = periodoInscricaoRepository.findByPeriodo("2026.1").orElseThrow();
        periodo.atualizarDatas(LocalDate.now().plusDays(1), LocalDate.now().plusDays(5));
        periodoInscricaoRepository.save(periodo);

        mockMvc.perform(post("/api/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson(aluno.getId(), List.of(disciplina1.getId()), List.of())))
                .andExpect(status().isConflict());
    }

    private String requestJson(UUID alunoId, List<UUID> obrigatorias, List<UUID> optativas) {
        String obrigatoriasJson = obrigatorias.stream()
                .map(id -> "\"" + id + "\"")
                .collect(java.util.stream.Collectors.joining(","));
        String optativasJson = optativas.stream()
                .map(id -> "\"" + id + "\"")
                .collect(java.util.stream.Collectors.joining(","));
        return """
                {"alunoId":"%s","periodo":"2026.1","disciplinasObrigatorias":[%s],"disciplinasOptativas":[%s]}
                """.formatted(alunoId, obrigatoriasJson, optativasJson);
    }
}
