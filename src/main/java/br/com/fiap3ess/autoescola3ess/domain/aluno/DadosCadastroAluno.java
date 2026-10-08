package br.com.fiap3ess.autoescola3ess.domain.aluno;

import br.com.fiap3ess.autoescola3ess.domain.endereco.DadosEndereco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record DadosCadastroAluno(

        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Informe um e-mail válido")
        String email,

        @NotBlank(message = "O telefone é obrigatório")
        String telefone,

        @NotBlank(message = "O CPF é obrigatório")
        @Pattern(
                regexp = "\\d{11}",
                message = "O CPF deve possuir 11 números"
        )
        String cpf,

        @NotNull(message = "O endereço é obrigatório")
        @Valid
        DadosEndereco endereco
) {
}