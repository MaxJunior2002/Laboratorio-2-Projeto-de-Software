package com.projetosft.labdois.modules.auth;

import com.projetosft.labdois.modules.aluno.domain.Aluno;
import com.projetosft.labdois.modules.professor.domain.Professor;
import com.projetosft.labdois.modules.auth.dto.LoginRequest;
import com.projetosft.labdois.modules.auth.dto.LoginResponse;
import com.projetosft.labdois.modules.auth.service.AuthService;
import com.projetosft.labdois.modules.aluno.repository.AlunoRepository;
import com.projetosft.labdois.modules.professor.repository.ProfessorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class AuthControllerTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @BeforeEach
    void setUp() {
        professorRepository.deleteAll();
        alunoRepository.deleteAll();
        alunoRepository.save(new Aluno("Maria Silva", "maria@email.com", "123456", "2025001"));
    }

    @Test
    void deveAutenticarUsuarioComCredenciaisValidas() {
        LoginResponse response = authService.autenticar(new LoginRequest("maria@email.com", "123456"));

        assertThat(response.getEmail()).isEqualTo("maria@email.com");
        assertThat(response.getNome()).isEqualTo("Maria Silva");
        assertThat(response.getPerfil()).isEqualTo("Aluno");
    }

    @Test
    void deveRecusarUsuarioComSenhaInvalida() {
        assertThatThrownBy(() -> authService.autenticar(new LoginRequest("maria@email.com", "senhaErrada")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Credenciais inválidas");
    }

    @Test
    void deveAutenticarProfessorComCredenciaisValidas() {
        professorRepository.save(new Professor("Carlos Lima", "carlos@email.com", "123456", "P-1001"));

        LoginResponse response = authService.autenticar(new LoginRequest("carlos@email.com", "123456"));

        assertThat(response.getEmail()).isEqualTo("carlos@email.com");
        assertThat(response.getPerfil()).isEqualTo("Professor");
    }
}
