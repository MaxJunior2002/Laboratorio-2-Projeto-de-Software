package com.projetosft.labdois.modules.auth.service;

import com.projetosft.labdois.modules.auth.dto.LoginRequest;
import com.projetosft.labdois.modules.auth.dto.LoginResponse;
import com.projetosft.labdois.modules.usuario.domain.Usuario;
import com.projetosft.labdois.modules.usuario.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UsuarioService usuarioService;

    public AuthService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public LoginResponse autenticar(LoginRequest request) {
        if (request == null || request.getEmail() == null || request.getSenha() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas.");
        }

        Usuario usuario = usuarioService.buscarPorEmail(request.getEmail());

        if (!usuarioService.validarSenha(usuario, request.getSenha())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas.");
        }

        return LoginResponse.from(usuario);
    }
}
