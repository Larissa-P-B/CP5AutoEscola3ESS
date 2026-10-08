package br.com.fiap3ess.autoescola3ess.domain.usuario;

import jakarta.validation.constraints.NotNull;

public record DadosAtualizacaoPerfil(

        @NotNull(message = "O perfil é obrigatório")
        Role perfil
) {
}