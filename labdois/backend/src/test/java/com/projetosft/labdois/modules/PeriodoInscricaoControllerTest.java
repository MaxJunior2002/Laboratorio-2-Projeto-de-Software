package com.projetosft.labdois.modules;

import com.projetosft.labdois.modules.aluno.domain.Aluno;
import com.projetosft.labdois.modules.aluno.repository.AlunoRepository;
import com.projetosft.labdois.modules.curso.domain.Curso;
import com.projetosft.labdois.modules.curso.repository.CursoRepository;
import com.projetosft.labdois.modules.disciplina.domain.Disciplina;
import com.projetosft.labdois.modules.disciplina.domain.StatusDisciplina;
import com.projetosft.labdois.modules.disciplina.repository.DisciplinaRepository;
import com.projetosft.labdois.modules.matricula.domain.Matricula;
import com.projetosft.labdois.modules.matricula.domain.PeriodoInscricao;
import com.projetosft.labdois.modules.matricula.domain.TipoDisciplinaMatricula;
import com.projetosft.labdois.modules.matricula.repository.MatriculaDisciplinaRepository;
import com.projetosft.labdois.modules.matricula.repository.MatriculaRepository;
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

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PeriodoInscricaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PeriodoInscricaoRepository periodoInscricaoRepository;

    @Autowired
    private MatriculaRepository matriculaRepository;

    @Autowired
    private MatriculaDisciplinaRepository matriculaDisciplinaRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @BeforeEach
    void limparPeriodos() {
        matriculaRepository.deleteAll();
        matriculaDisciplinaRepository.deleteAll();
        disciplinaRepository.deleteAll();
        alunoRepository.deleteAll();
        professorRepository.deleteAll();
        cursoRepository.deleteAll();
        periodoInscricaoRepository.deleteAll();
    }

    @Test
    void deveCadastrarConsultarEAtualizarJanelaDeInscricao() throws Exception {
        mockMvc.perform(post("/api/periodos-inscricao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"periodo":"2026.2","inicio":"2026-10-01","fim":"2026-10-31"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.periodo").value("2026.2"));

        mockMvc.perform(get("/api/periodos-inscricao/2026.2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inicio").value("2026-10-01"))
                .andExpect(jsonPath("$.fim").value("2026-10-31"));

        mockMvc.perform(put("/api/periodos-inscricao/2026.2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"periodo":"2026.2","inicio":"2026-10-05","fim":"2026-11-05"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inicio").value("2026-10-05"))
                .andExpect(jsonPath("$.fim").value("2026-11-05"));
    }

    @Test
    void deveRejeitarDatasInvalidasEPeriodoDuplicado() throws Exception {
        String body = """
                {"periodo":"2026.2","inicio":"2026-10-01","fim":"2026-10-31"}
                """;
        mockMvc.perform(post("/api/periodos-inscricao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/periodos-inscricao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/periodos-inscricao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"periodo":"2026.3","inicio":"2026-11-30","fim":"2026-11-01"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveAtivarDisciplinasComTresAlunosECancelarAsDemaisAoEncerrar() throws Exception {
        periodoInscricaoRepository.save(new PeriodoInscricao(
                "2026.1", LocalDate.of(2020, 1, 1), LocalDate.of(2020, 6, 30)));
        Curso curso = cursoRepository.save(new Curso("Engenharia de Software", 40));
        Professor professor = professorRepository.save(
                new Professor("Carlos Lima", "carlos@example.com", "senha", "P-1001"));
        Disciplina ativa = disciplinaRepository.save(new Disciplina("Programação", 60, curso, professor));
        Disciplina cancelada = disciplinaRepository.save(new Disciplina("Banco de Dados", 60, curso, professor));

        java.util.List<Aluno> alunosAtivos = criarMatriculas(ativa, 3);
        java.util.List<Aluno> alunosCancelados = criarMatriculas(cancelada, 2);

        mockMvc.perform(post("/api/periodos-inscricao/2026.1/encerrar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.periodo").value("2026.1"))
                .andExpect(jsonPath("$.disciplinasAtivadas").value(1))
                .andExpect(jsonPath("$.disciplinasCanceladas").value(1));

        org.junit.jupiter.api.Assertions.assertEquals(StatusDisciplina.ATIVA,
                disciplinaRepository.findById(ativa.getId()).orElseThrow().getStatus());
        org.junit.jupiter.api.Assertions.assertEquals(StatusDisciplina.CANCELADA,
                disciplinaRepository.findById(cancelada.getId()).orElseThrow().getStatus());
        org.junit.jupiter.api.Assertions.assertTrue(
                periodoInscricaoRepository.findByPeriodo("2026.1").orElseThrow().isEncerrado());

        periodoInscricaoRepository.save(new PeriodoInscricao(
                "2026.2", LocalDate.now().minusDays(1), LocalDate.now().plusDays(1)));
        mockMvc.perform(post("/api/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"alunoId":"%s","periodo":"2026.2","disciplinasObrigatorias":["%s"],"disciplinasOptativas":[]}
                                """.formatted(alunosAtivos.getFirst().getId(), ativa.getId())))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"alunoId":"%s","periodo":"2026.2","disciplinasObrigatorias":["%s"],"disciplinasOptativas":[]}
                                """.formatted(alunosCancelados.getFirst().getId(), cancelada.getId())))
                .andExpect(status().isConflict());
    }

    @Test
    void naoDeveEncerrarPeriodoAntesDoFimNemDuasVezes() throws Exception {
        periodoInscricaoRepository.save(new PeriodoInscricao(
                "2026.1", LocalDate.now().minusDays(1), LocalDate.now()));

        mockMvc.perform(post("/api/periodos-inscricao/2026.1/encerrar"))
                .andExpect(status().isConflict());

        PeriodoInscricao periodo = periodoInscricaoRepository.findByPeriodo("2026.1").orElseThrow();
        periodo.atualizarDatas(LocalDate.now().minusDays(3), LocalDate.now().minusDays(1));
        periodoInscricaoRepository.save(periodo);

        mockMvc.perform(post("/api/periodos-inscricao/2026.1/encerrar"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/periodos-inscricao/2026.1/encerrar"))
                .andExpect(status().isConflict());
        mockMvc.perform(put("/api/periodos-inscricao/2026.1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"periodo":"2026.1","inicio":"2026-01-01","fim":"2026-12-31"}
                                """))
                .andExpect(status().isConflict());
    }

    private java.util.List<Aluno> criarMatriculas(Disciplina disciplina, int quantidade) {
        java.util.List<Aluno> alunos = new java.util.ArrayList<>();
        for (int indice = 0; indice < quantidade; indice++) {
            Aluno aluno = alunoRepository.save(new Aluno(
                    "Aluno " + disciplina.getNome() + indice,
                    disciplina.getNome() + indice + "@example.com",
                    "senha",
                    "2026" + disciplina.getNome().hashCode() + indice));
            alunos.add(aluno);
            Matricula matricula = new Matricula("2026.1", LocalDate.now(), aluno);
            matricula.adicionarDisciplina(disciplina, TipoDisciplinaMatricula.OBRIGATORIA);
            matriculaRepository.save(matricula);
        }
        return alunos;
    }
}
