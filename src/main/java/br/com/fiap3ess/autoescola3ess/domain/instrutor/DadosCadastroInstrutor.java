package br.com.fiap3ess.autoescola3ess.domain.instrutor;

import br.com.fiap3ess.autoescola3ess.domain.endereco.DadosEndereco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record DadosCadastroInstrutor(

        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Informe um e-mail válido")
        String email,

        @NotBlank(message = "O telefone é obrigatório")
        String telefone,

        @NotBlank(message = "A CNH é obrigatória")
        @Pattern(
                regexp = "[0-9]{9,11}",
                message = "A CNH deve possuir entre 9 e 11 números"
        )
        String cnh,

        @NotNull(message = "A especialidade é obrigatória")
        Especialidade especialidade,

        @NotNull(message = "O endereço é obrigatório")
        @Valid
        DadosEndereco endereco
) {
}