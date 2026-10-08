package br.com.fiap3ess.autoescola3ess.domain.agenda;

import br.com.fiap3ess.autoescola3ess.domain.instrutor.Especialidade;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record DadosDetalhamentoAgendamento(
        Long id,

        @JsonProperty("nome_aluno")
        String nomeAluno,

        @JsonProperty("nome_instrutor")
        String nomeInstrutor,

        Especialidade especialidade,

        @JsonProperty("data_hora")
        @JsonFormat(pattern = "dd/MM/yyyy - HH:mm")
        LocalDateTime dataHora,

        StatusInstrucao status,

        @JsonProperty("motivo_cancelamento")
        MotivoCancelamento motivoCancelamento,

        @JsonProperty("data_cancelamento")
        @JsonFormat(pattern = "dd/MM/yyyy - HH:mm")
        LocalDateTime dataCancelamento
) {

    public DadosDetalhamentoAgendamento(Instrucao instrucao) {
        this(
                instrucao.getId(),
                instrucao.getAluno().getNome(),
                instrucao.getInstrutor().getNome(),
                instrucao.getInstrutor().getEspecialidade(),
                instrucao.getDataHora(),
                instrucao.getStatus(),
                instrucao.getMotivoCancelamento(),
                instrucao.getDataCancelamento()
        );
    }
}