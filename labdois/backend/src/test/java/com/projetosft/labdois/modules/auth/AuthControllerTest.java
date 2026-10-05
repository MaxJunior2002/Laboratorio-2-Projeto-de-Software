package com.projetosft.labdois.modules.auth;

import com.projetosft.labdois.modules.aluno.domain.Aluno;
import com.projetosft.labdois.modules.auth.dto.LoginRequest;
import com.projetosft.labdois.modules.auth.dto.LoginResponse;
import com.projetosft.labdois.modules.auth.service.AuthService;
import com.projetosft.labdois.modules.usuario.domain.Usuario;
import com.projetosft.labdois.modules.usuario.repository.UsuarioRepository;
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
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        Usuario aluno = new Aluno("Maria Silva", "maria@email.com", "123456", "2025001");
        usuarioRepository.save(aluno);
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
}
