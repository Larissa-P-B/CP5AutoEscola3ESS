package br.com.fiap3ess.autoescola3ess;

import br.com.fiap3ess.autoescola3ess.domain.agenda.*;
import br.com.fiap3ess.autoescola3ess.domain.agenda.validacao.*;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.*;

class ValidadoresAgendamentoTest {
    private DadosAgendamento dados(LocalDateTime h) { return new DadosAgendamento(1L, 1L, null, h); }
    @ParameterizedTest @ValueSource(ints = {0, 5, 21, 23}) void recusarForaDoFuncionamento(int hora) {
        assertThatThrownBy(() -> new ValidadorHorarioFuncionamento().validar(dados(LocalDateTime.of(2030, 1, 7, hora, 0))))
                .isInstanceOf(ValidacaoException.class);
    }
    @ParameterizedTest @ValueSource(ints = {6, 10, 20}) void aceitarHorarioDeFuncionamento(int hora) {
        assertThatCode(() -> new ValidadorHorarioFuncionamento().validar(dados(LocalDateTime.of(2030, 1, 7, hora, 0))))
                .doesNotThrowAnyException();
    }
    @Test void recusarDomingo() {
        assertThatThrownBy(() -> new ValidadorHorarioFuncionamento().validar(dados(LocalDateTime.of(2030, 1, 6, 10, 0))))
                .isInstanceOf(ValidacaoException.class);
    }
    @Test void recusarMinutosESegundosFracionados() {
        assertThatThrownBy(() -> new ValidadorHoraInteira().validar(dados(LocalDateTime.of(2030, 1, 7, 10, 30))))
                .isInstanceOf(ValidacaoException.class);
        assertThatThrownBy(() -> new ValidadorHoraInteira().validar(dados(LocalDateTime.of(2030, 1, 7, 10, 0, 1))))
                .isInstanceOf(ValidacaoException.class);
    }
    @Test void recusarPoucaAntecedencia() {
        assertThatThrownBy(() -> new ValidadorHorarioAntecedencia().validar(dados(LocalDateTime.now().plusMinutes(10))))
                .isInstanceOf(ValidacaoException.class);
    }
}
