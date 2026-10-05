package com.projetosft.labdois.modules;

import com.projetosft.labdois.modules.aluno.repository.AlunoRepository;
import com.projetosft.labdois.modules.professor.repository.ProfessorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CadastroPessoasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @BeforeEach
    void limparCadastros() {
        professorRepository.deleteAll();
        alunoRepository.deleteAll();
    }

    @Test
    void deveCriarAlunoPelaRotaDoDominio() throws Exception {
        mockMvc.perform(post("/api/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Ana Souza","email":"ana@email.com","senha":"123456","numeroMatricula":"2025001"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("ana@email.com"))
                .andExpect(jsonPath("$.perfil").value("Aluno"));
    }

    @Test
    void deveCriarProfessorPelaRotaDoDominio() throws Exception {
        mockMvc.perform(post("/api/professores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Carlos Lima","email":"carlos@email.com","senha":"123456","identificadorFuncional":"P-1001"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("carlos@email.com"))
                .andExpect(jsonPath("$.perfil").value("Professor"));
    }

    @Test
    void deveImpedirEmailDuplicadoEntreDominios() throws Exception {
        mockMvc.perform(post("/api/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Ana Souza","email":"compartilhado@email.com","senha":"123456","numeroMatricula":"2025001"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/professores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Carlos Lima","email":"compartilhado@email.com","senha":"123456","identificadorFuncional":"P-1001"}
                                """))
                .andExpect(status().isConflict());
    }
}