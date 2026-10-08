package br.com.fiap3ess.autoescola3ess.domain.agenda;

import br.com.fiap3ess.autoescola3ess.domain.aluno.Aluno;
import br.com.fiap3ess.autoescola3ess.domain.instrutor.Instrutor;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "Instrucao")
@Table(name = "instrucoes")
@NoArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class Instrucao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instrutor_id")
    private Instrutor instrutor;

    @Column(name = "data_hora")
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    private StatusInstrucao status;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo_cancelamento")
    private MotivoCancelamento motivoCancelamento;

    @Column(name = "data_cancelamento")
    private LocalDateTime dataCancelamento;

    public Instrucao(
            Aluno aluno,
            Instrutor instrutor,
            LocalDateTime dataHora
    ) {
        this.aluno = aluno;
        this.instrutor = instrutor;
        this.dataHora = dataHora;
        this.status = StatusInstrucao.AGENDADA;
    }

    public void reagendar(Aluno aluno, Instrutor instrutor, LocalDateTime dataHora) {
        this.aluno = aluno;
        this.instrutor = instrutor;
        this.dataHora = dataHora;
    }

    public void cancelar(MotivoCancelamento motivo) {
        this.status = StatusInstrucao.CANCELADA;
        this.motivoCancelamento = motivo;
        this.dataCancelamento = LocalDateTime.now();
    }
}