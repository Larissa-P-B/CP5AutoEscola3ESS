package br.com.fiap3ess.autoescola3ess.domain.agenda;

import jakarta.validation.constraints.NotNull;

public record DadosCancelamento(

        @NotNull(message = "O motivo do cancelamento é obrigatório")
        MotivoCancelamento motivo
) {
}