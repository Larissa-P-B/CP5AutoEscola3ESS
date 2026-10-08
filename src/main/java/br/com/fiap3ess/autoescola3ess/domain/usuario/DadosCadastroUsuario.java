package br.com.fiap3ess.autoescola3ess.domain.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DadosCadastroUsuario(

        @NotBlank(message = "O login é obrigatório")
        String login,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, message = "A senha deve possuir pelo menos 6 caracteres")
        String senha,

        @NotNull(message = "O perfil é obrigatório")
        Role perfil
) {
}