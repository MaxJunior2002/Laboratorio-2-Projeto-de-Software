package com.projetosft.labdois.modules.secretaria.domain;

import com.projetosft.labdois.modules.usuario.domain.Usuario;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "secretarias")
public class Secretaria extends Usuario {

    protected Secretaria() {
    }

    public Secretaria(String nome, String email, String senha) {
        super(nome, email, senha);
    }
}