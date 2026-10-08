package br.com.fiap3ess.autoescola3ess.domain.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DadosAlteracaoSenha(

        @NotBlank(message = "A senha atual é obrigatória")
        String senhaAtual,

        @NotBlank(message = "A nova senha é obrigatória")
        @Size(
                min = 6,
                message = "A nova senha deve possuir pelo menos 6 caracteres"
        )
        String novaSenha
) {
}