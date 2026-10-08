package br.com.fiap3ess.autoescola3ess.domain.agenda;

import br.com.fiap3ess.autoescola3ess.domain.instrutor.Especialidade;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record DadosAgendamento(
        @NotNull
        @JsonProperty("id_aluno")
        //@JsonAlias("id_aluno") //Alternativa à @JsonProperty
        Long idAluno,

        @JsonProperty("id_instrutor")
        Long idInstrutor,
        Especialidade especialidade,

        @NotNull
        @JsonProperty("data_hora")
        @JsonFormat(pattern = "dd/MM/yyyy - HH:mm")
        @Future
        LocalDateTime dataHora) {
}