package com.projetosft.labdois.modules.usuario.service;

import com.projetosft.labdois.modules.usuario.domain.Usuario;
import com.projetosft.labdois.modules.usuario.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario buscarPorEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas.");
        }

        return usuarioRepository.findByEmail(email.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas."));
    }

    public boolean validarSenha(Usuario usuario, String senhaInformada) {
        if (usuario == null || senhaInformada == null) {
            return false;
        }

        return usuario.getSenha() != null && usuario.getSenha().equals(senhaInformada);
    }
}
