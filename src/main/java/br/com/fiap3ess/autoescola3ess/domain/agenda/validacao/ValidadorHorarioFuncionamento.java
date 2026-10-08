package br.com.fiap3ess.autoescola3ess.domain.agenda.validacao;

import br.com.fiap3ess.autoescola3ess.domain.agenda.DadosAgendamento;
import br.com.fiap3ess.autoescola3ess.domain.agenda.ValidacaoException;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;

@Component
public class ValidadorHorarioFuncionamento
        implements ValidadorAgendamento {

    @Override
    public void validar(DadosAgendamento dados) {
        DayOfWeek diaDaSemana =
                dados.dataHora().getDayOfWeek();

        int hora = dados.dataHora().getHour();

        boolean domingo =
                diaDaSemana == DayOfWeek.SUNDAY;

        boolean antesDaAbertura =
                hora < 6;

        /*
         * A instrução dura uma hora.
         * Portanto, o último início permitido é às 20:00,
         * terminando às 21:00.
         */
        boolean depoisDoFechamento =
                hora >= 21;

        if (
                domingo
                        || antesDaAbertura
                        || depoisDoFechamento
        ) {
            throw new ValidacaoException(
                    "A autoescola funciona de segunda a sábado, " +
                            "das 06:00 às 21:00!"
            );
        }
    }
}