package br.com.fiap3ess.autoescola3ess.domain.agenda.validacao;

import br.com.fiap3ess.autoescola3ess.domain.agenda.DadosAgendamento;
import br.com.fiap3ess.autoescola3ess.domain.agenda.InstrucaoRepository;
import br.com.fiap3ess.autoescola3ess.domain.agenda.StatusInstrucao;
import br.com.fiap3ess.autoescola3ess.domain.agenda.ValidacaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidadorConflitoHorarioInstrutor
        implements ValidadorAgendamento {

    @Autowired
    private InstrucaoRepository repository;

    @Override
    public void validar(DadosAgendamento dados) {
        if (dados.idInstrutor() == null) {
            return;
        }

        boolean instrutorOcupado =
                repository.existsByInstrutorIdAndDataHoraAndStatus(
                        dados.idInstrutor(),
                        dados.dataHora(),
                        StatusInstrucao.AGENDADA
                );

        if (instrutorOcupado) {
            throw new ValidacaoException(
                    "O instrutor já possui uma instrução " +
                            "agendada nessa data e horário!"
            );
        }
    }
    @Override
    public void validarAtualizacao(DadosAgendamento dados, Long idInstrucao) {
        if (dados.idInstrutor() != null && repository.existsByInstrutorIdAndDataHoraAndStatusAndIdNot(
                dados.idInstrutor(), dados.dataHora(), StatusInstrucao.AGENDADA, idInstrucao)) {
            throw new ValidacaoException("O instrutor já possui uma instrução agendada nessa data e horário!");
        }
    }
}