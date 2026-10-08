package br.com.fiap3ess.autoescola3ess.domain.agenda.validacao;

import br.com.fiap3ess.autoescola3ess.domain.agenda.DadosAgendamento;
import br.com.fiap3ess.autoescola3ess.domain.agenda.ValidacaoException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ValidadorHoraInteira implements ValidadorAgendamento {
    @Override
    public void validar(DadosAgendamento dados) {
        LocalDateTime dataEscolhida = dados.dataHora();

        if (dataEscolhida.getMinute() != 0 || dataEscolhida.getSecond() != 0 || dataEscolhida.getNano() != 0) {
            throw new ValidacaoException("O campo precisa ser preenchido com horas inteiras (ex: 09:00, 13:00, ...)");
        }
    }
}