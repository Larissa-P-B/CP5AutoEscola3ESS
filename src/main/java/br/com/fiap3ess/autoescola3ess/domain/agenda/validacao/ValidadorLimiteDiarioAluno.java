package br.com.fiap3ess.autoescola3ess.domain.agenda.validacao;

import br.com.fiap3ess.autoescola3ess.domain.agenda.DadosAgendamento;
import br.com.fiap3ess.autoescola3ess.domain.agenda.InstrucaoRepository;
import br.com.fiap3ess.autoescola3ess.domain.agenda.StatusInstrucao;
import br.com.fiap3ess.autoescola3ess.domain.agenda.ValidacaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ValidadorLimiteDiarioAluno
        implements ValidadorAgendamento {

    @Autowired
    private InstrucaoRepository repository;

    @Override
    public void validar(DadosAgendamento dados) {
        LocalDateTime inicioDoDia = dados.dataHora()
                .toLocalDate()
                .atStartOfDay();

        LocalDateTime fimDoDia = inicioDoDia
                .plusDays(1)
                .minusNanos(1);

        long quantidadeDeInstrucoes =
                repository.countByAlunoIdAndDataHoraBetweenAndStatus(
                        dados.idAluno(),
                        inicioDoDia,
                        fimDoDia,
                        StatusInstrucao.AGENDADA
                );

        if (quantidadeDeInstrucoes >= 2) {
            throw new ValidacaoException(
                    "O aluno não pode agendar mais de duas " +
                            "instruções no mesmo dia!"
            );
        }
    }
    @Override
    public void validarAtualizacao(DadosAgendamento dados, Long idInstrucao) {
        LocalDateTime inicio = dados.dataHora().toLocalDate().atStartOfDay();
        long quantidade = repository.countByAlunoIdAndDataHoraBetweenAndStatusAndIdNot(
                dados.idAluno(), inicio, inicio.plusDays(1).minusNanos(1), StatusInstrucao.AGENDADA, idInstrucao);
        if (quantidade >= 2) {
            throw new ValidacaoException("O aluno não pode agendar mais de duas instruções no mesmo dia!");
        }
    }
}