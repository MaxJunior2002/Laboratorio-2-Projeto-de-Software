package com.projetosft.labdois.modules;

import com.projetosft.labdois.modules.matricula.repository.PeriodoInscricaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

    @BeforeEach
    void limparPeriodos() {
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
}
