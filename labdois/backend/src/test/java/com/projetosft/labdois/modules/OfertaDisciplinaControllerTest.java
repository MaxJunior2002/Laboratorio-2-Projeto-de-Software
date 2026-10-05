package com.projetosft.labdois.modules;

import com.projetosft.labdois.modules.aluno.domain.Aluno;
import com.projetosft.labdois.modules.aluno.repository.AlunoRepository;
import com.projetosft.labdois.modules.cobranca.repository.NotificacaoCobrancaRepository;
import com.projetosft.labdois.modules.curso.domain.Curso;
import com.projetosft.labdois.modules.curso.repository.CursoRepository;
import com.projetosft.labdois.modules.disciplina.domain.Disciplina;
import com.projetosft.labdois.modules.disciplina.domain.OfertaDisciplina;
import com.projetosft.labdois.modules.disciplina.repository.DisciplinaRepository;
import com.projetosft.labdois.modules.disciplina.repository.OfertaDisciplinaRepository;
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
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OfertaDisciplinaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private OfertaDisciplinaRepository ofertaRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private MatriculaRepository matriculaRepository;

    @Autowired
    private MatriculaDisciplinaRepository matriculaDisciplinaRepository;

    @Autowired
    private PeriodoInscricaoRepository periodoRepository;

    @Autowired
    private NotificacaoCobrancaRepository notificacaoCobrancaRepository;

    @BeforeEach
    void limparDados() {
        notificacaoCobrancaRepository.deleteAll();
        matriculaRepository.deleteAll();
        matriculaDisciplinaRepository.deleteAll();
        ofertaRepository.deleteAll();
        disciplinaRepository.deleteAll();
        periodoRepository.deleteAll();
        alunoRepository.deleteAll();
        professorRepository.deleteAll();
        cursoRepository.deleteAll();
    }

    @Test
    void secretariaPodeCriarOfertaParaPeriodoEDefinirProfessorEVagas() throws Exception {
        Professor professor = criarProfessor("Ana Souza", "ana@example.com");
        Disciplina disciplina = criarDisciplina(professor);
        criarPeriodo("2026.2");

        mockMvc.perform(post("/api/ofertas-disciplinas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ofertaJson(disciplina.getId(), professor.getId(), "2026.2")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.disciplinaId").value(disciplina.getId().toString()))
                .andExpect(jsonPath("$.periodo").value("2026.2"))
                .andExpect(jsonPath("$.professorId").value(professor.getId().toString()))
                .andExpect(jsonPath("$.capacidadeMaxima").value(60))
                .andExpect(jsonPath("$.minimoAlunos").value(3));

        mockMvc.perform(get("/api/ofertas-disciplinas").param("periodo", "2026.2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void professorConsultaSomenteAlunosAtivosDasSuasOfertasNoPeriodo() throws Exception {
        Professor professor = criarProfessor("Ana Souza", "ana@example.com");
        Disciplina disciplina = criarDisciplina(professor);
        PeriodoInscricao periodo = criarPeriodo("2026.2");
        OfertaDisciplina oferta = ofertaRepository.save(
                new OfertaDisciplina(disciplina, periodo, professor, 60, 3));
        Aluno aluno = alunoRepository.save(new Aluno("Caio Silva", "caio@example.com", "segredo", "2026001"));
        Matricula matricula = new Matricula("2026.2", LocalDate.now(), aluno);
        matricula.adicionarDisciplina(oferta, TipoDisciplinaMatricula.OBRIGATORIA);
        matriculaRepository.save(matricula);

        mockMvc.perform(get("/api/professores/{id}/turmas", professor.getId())
                        .param("periodo", "2026.2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ofertaId").value(oferta.getId().toString()))
                .andExpect(jsonPath("$[0].disciplinaNome").value("Programação"))
                .andExpect(jsonPath("$[0].alunos[0].id").value(aluno.getId().toString()))
                .andExpect(jsonPath("$[0].alunos[0].nome").value("Caio Silva"))
                .andExpect(jsonPath("$[0].alunos[0].senha").doesNotExist());
    }

    @Test
    void naoDeveDuplicarOfertaDaMesmaDisciplinaNoMesmoPeriodo() throws Exception {
        Professor professor = criarProfessor("Ana Souza", "ana@example.com");
        Disciplina disciplina = criarDisciplina(professor);
        criarPeriodo("2026.2");
        String body = ofertaJson(disciplina.getId(), professor.getId(), "2026.2");

        mockMvc.perform(post("/api/ofertas-disciplinas")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/ofertas-disciplinas")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    private Professor criarProfessor(String nome, String email) {
        return professorRepository.save(new Professor(nome, email, "senha", UUID.randomUUID().toString()));
    }

    private Disciplina criarDisciplina(Professor professor) {
        Curso curso = cursoRepository.save(new Curso("Engenharia de Software", 40));
        return disciplinaRepository.save(new Disciplina("Programação", 60, curso, professor));
    }

    private PeriodoInscricao criarPeriodo(String periodo) {
        return periodoRepository.save(new PeriodoInscricao(
                periodo, LocalDate.now().minusDays(1), LocalDate.now().plusDays(30)));
    }

    private String ofertaJson(UUID disciplinaId, UUID professorId, String periodo) {
        return """
                {"disciplinaId":"%s","periodo":"%s","professorId":"%s","capacidadeMaxima":60,"minimoAlunos":3}
                """.formatted(disciplinaId, periodo, professorId);
    }
}
