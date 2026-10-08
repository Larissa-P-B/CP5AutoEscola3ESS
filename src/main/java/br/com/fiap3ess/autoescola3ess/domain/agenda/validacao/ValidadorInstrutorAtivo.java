package br.com.fiap3ess.autoescola3ess.domain.agenda.validacao;

import br.com.fiap3ess.autoescola3ess.domain.agenda.DadosAgendamento;
import br.com.fiap3ess.autoescola3ess.domain.agenda.ValidacaoException;
import br.com.fiap3ess.autoescola3ess.domain.instrutor.InstrutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidadorInstrutorAtivo
        implements ValidadorAgendamento {

    @Autowired
    private InstrutorRepository instrutorRepository;

    @Override
    public void validar(DadosAgendamento dados) {
        if (dados.idInstrutor() == null) {
            return;
        }

        boolean instrutorInativo =
                instrutorRepository.existsByIdAndAtivoFalse(
                        dados.idInstrutor()
                );

        if (instrutorInativo) {
            throw new ValidacaoException(
                    "Não é possível agendar uma instrução " +
                            "com um instrutor inativo!"
            );
        }
    }
}