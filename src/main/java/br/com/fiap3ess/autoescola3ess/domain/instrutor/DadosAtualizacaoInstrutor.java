package br.com.fiap3ess.autoescola3ess.domain.instrutor;

import br.com.fiap3ess.autoescola3ess.domain.endereco.DadosEndereco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record DadosAtualizacaoInstrutor(

        @NotNull(message = "O ID do instrutor é obrigatório")
        Long id,

        String nome,

        String telefone,

        @Valid
        DadosEndereco endereco
) {
}