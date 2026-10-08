package br.com.fiap3ess.autoescola3ess.service;

import br.com.fiap3ess.autoescola3ess.domain.agenda.*;
import br.com.fiap3ess.autoescola3ess.domain.agenda.validacao.ValidadorAgendamento;
import br.com.fiap3ess.autoescola3ess.domain.aluno.Aluno;
import br.com.fiap3ess.autoescola3ess.domain.aluno.AlunoNotFoundException;
import br.com.fiap3ess.autoescola3ess.domain.aluno.AlunoRepository;
import br.com.fiap3ess.autoescola3ess.domain.instrutor.Instrutor;
import br.com.fiap3ess.autoescola3ess.domain.instrutor.InstrutorNotFoundException;
import br.com.fiap3ess.autoescola3ess.domain.instrutor.InstrutorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgendaDeInstrucoes {
    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private InstrutorRepository instrutorRepository;

    @Autowired
    private InstrucaoRepository repository;

    @Autowired
    private List<ValidadorAgendamento> validadoresAgendamento;

    @Transactional
    public DadosDetalhamentoAgendamento agendar(DadosAgendamento dados) {
        if (!alunoRepository.existsById(dados.idAluno())) {
            throw new AlunoNotFoundException("ID do aluno informado não existe!");
        }
        if (dados.idInstrutor() != null && !instrutorRepository.existsById(dados.idInstrutor())) {
            throw new InstrutorNotFoundException("ID do instrutor informado não existe!");
        }
        //Validações
        validadoresAgendamento.forEach(validador -> validador.validar(dados));

        Aluno aluno = alunoRepository.getReferenceById(dados.idAluno());

        Instrutor instrutor = escolherInstrutor(dados);
        if (instrutor == null) {
            throw new ValidacaoException("Nenhum instrutor disponível para a data/hora informada!");
        }

        Instrucao instrucao = new Instrucao(aluno, instrutor, dados.dataHora());
        Instrucao salva = repository.save(instrucao);
        return new DadosDetalhamentoAgendamento(salva);
    }

    private Instrutor escolherInstrutor(DadosAgendamento dados) {
        if (dados.idInstrutor() != null) {
            return instrutorRepository.getReferenceById(dados.idInstrutor());
        }
        if (dados.especialidade() == null) {
            throw new ValidacaoException("Especialidade é campo obrigatório, caso o instrutor não seja informado!");
        }
        return instrutorRepository.escolherInstrutorAleatorioDisponivel(
                dados.especialidade(),
                dados.dataHora(),
                StatusInstrucao.AGENDADA
        );
    }

    @Transactional
    public DadosDetalhamentoAgendamento cancelar(
            Long id,
            DadosCancelamento dados
    ) {
        Instrucao instrucao = repository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        if (instrucao.getStatus() == StatusInstrucao.CANCELADA) {
            throw new ValidacaoException(
                    "Essa instrução já está cancelada!"
            );
        }

        LocalDateTime limiteParaCancelamento =
                LocalDateTime.now().plusHours(24);

        if (instrucao.getDataHora()
                .isBefore(limiteParaCancelamento)) {

            throw new ValidacaoException(
                    "A instrução somente pode ser cancelada " +
                            "com antecedência mínima de 24 horas!"
            );
        }

        instrucao.cancelar(dados.motivo());

        return new DadosDetalhamentoAgendamento(instrucao);
    }
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<DadosDetalhamentoAgendamento> listar(
            org.springframework.data.domain.Pageable paginacao) {
        return repository.findAll(paginacao).map(DadosDetalhamentoAgendamento::new);
    }

    @Transactional(readOnly = true)
    public DadosDetalhamentoAgendamento detalhar(Long id) {
        return new DadosDetalhamentoAgendamento(repository.findById(id)
                .orElseThrow(EntityNotFoundException::new));
    }

    @Transactional
    public DadosDetalhamentoAgendamento atualizar(Long id, DadosAgendamento dados) {
        Instrucao instrucao = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (instrucao.getStatus() == StatusInstrucao.CANCELADA) {
            throw new ValidacaoException("Não é possível atualizar uma instrução cancelada!");
        }
        if (instrucao.getDataHora().isBefore(LocalDateTime.now().plusHours(24))) {
            throw new ValidacaoException("O reagendamento exige antecedência mínima de 24 horas da instrução original!");
        }
        // No PUT, informe explicitamente o instrutor. Assim a validação exclui o próprio registro.
        if (dados.idInstrutor() == null) {
            throw new ValidacaoException("Informe o id_instrutor para atualizar a instrução!");
        }
        if (!alunoRepository.existsById(dados.idAluno())) {
            throw new AlunoNotFoundException("ID do aluno informado não existe!");
        }
        if (!instrutorRepository.existsById(dados.idInstrutor())) {
            throw new InstrutorNotFoundException("ID do instrutor informado não existe!");
        }
        validadoresAgendamento.forEach(validador -> validador.validarAtualizacao(dados, id));
        instrucao.reagendar(alunoRepository.getReferenceById(dados.idAluno()),
                instrutorRepository.getReferenceById(dados.idInstrutor()), dados.dataHora());
        return new DadosDetalhamentoAgendamento(instrucao);
    }
}