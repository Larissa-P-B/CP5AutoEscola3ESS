package br.com.fiap3ess.autoescola3ess.controller;

import br.com.fiap3ess.autoescola3ess.domain.agenda.DadosAgendamento;
import br.com.fiap3ess.autoescola3ess.domain.agenda.DadosCancelamento;
import br.com.fiap3ess.autoescola3ess.domain.agenda.DadosDetalhamentoAgendamento;
import br.com.fiap3ess.autoescola3ess.service.AgendaDeInstrucoes;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@io.swagger.v3.oas.annotations.tags.Tag(name = "Instruções")
@RestController
@RequestMapping("/instrucoes")
public class InstrucaoController {
    @Autowired
    private AgendaDeInstrucoes agenda;

    @PostMapping
    public ResponseEntity<DadosDetalhamentoAgendamento> agendarInstrucao(@RequestBody @Valid DadosAgendamento dados) {
        return ResponseEntity.ok(agenda.agendar(dados));
    }

    @PatchMapping("/{id}/cancelamento")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<DadosDetalhamentoAgendamento> cancelar(
            @PathVariable Long id,
            @RequestBody @Valid DadosCancelamento dados
    ) {
        DadosDetalhamentoAgendamento resultado =
                agenda.cancelar(id, dados);

        return ResponseEntity.ok(resultado);
    }
    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<DadosDetalhamentoAgendamento>> listar(
            @org.springframework.data.web.PageableDefault(size = 10, sort = "dataHora")
            org.springframework.data.domain.Pageable paginacao) {
        return ResponseEntity.ok(agenda.listar(paginacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoAgendamento> detalhar(@PathVariable Long id) {
        return ResponseEntity.ok(agenda.detalhar(id));
    }

    @PutMapping("/{id}")
    @io.swagger.v3.oas.annotations.Operation(summary = "Reagendar uma instrução",
            description = "Exige id_aluno, id_instrutor e data_hora. Antecedência de 24h em relação ao horário original.")
    public ResponseEntity<DadosDetalhamentoAgendamento> atualizar(
            @PathVariable Long id, @RequestBody @Valid DadosAgendamento dados) {
        return ResponseEntity.ok(agenda.atualizar(id, dados));
    }
}