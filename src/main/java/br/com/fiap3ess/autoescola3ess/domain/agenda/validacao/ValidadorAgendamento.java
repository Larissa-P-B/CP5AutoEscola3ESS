package br.com.fiap3ess.autoescola3ess.domain.agenda.validacao;

import br.com.fiap3ess.autoescola3ess.domain.agenda.DadosAgendamento;

public interface ValidadorAgendamento {
    void validar(DadosAgendamento dados);

    default void validarAtualizacao(DadosAgendamento dados, Long idInstrucao) {
        validar(dados);
    }
}