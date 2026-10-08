package br.com.fiap3ess.autoescola3ess.domain.agenda;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface InstrucaoRepository
        extends JpaRepository<Instrucao, Long> {

    boolean existsByInstrutorIdAndDataHoraAndStatusAndIdNot(
            Long idInstrutor, LocalDateTime dataHora, StatusInstrucao status, Long id);

    long countByAlunoIdAndDataHoraBetweenAndStatusAndIdNot(
            Long idAluno, LocalDateTime inicio, LocalDateTime fim, StatusInstrucao status, Long id);

    boolean existsByInstrutorIdAndDataHoraAndStatus(
            Long idInstrutor,
            LocalDateTime dataHora,
            StatusInstrucao status
    );

    long countByAlunoIdAndDataHoraBetweenAndStatus(
            Long idAluno,
            LocalDateTime inicioDoDia,
            LocalDateTime fimDoDia,
            StatusInstrucao status
    );
}