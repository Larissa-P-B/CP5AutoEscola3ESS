package br.com.fiap3ess.autoescola3ess.domain.aluno;

import br.com.fiap3ess.autoescola3ess.domain.endereco.DadosEndereco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record DadosAtualizacaoAluno(

        @NotNull(message = "O ID do aluno é obrigatório")
        Long id,

        String nome,

        String telefone,

        @Valid
        DadosEndereco endereco
) {
}