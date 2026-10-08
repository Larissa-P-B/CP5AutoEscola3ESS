package br.com.fiap3ess.autoescola3ess.controller;

import br.com.fiap3ess.autoescola3ess.domain.agenda.ValidacaoException;
import br.com.fiap3ess.autoescola3ess.domain.instrutor.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.springframework.data.domain.Sort.Direction.ASC;

@io.swagger.v3.oas.annotations.tags.Tag(name = "Instrutores")
@RestController
@RequestMapping("/instrutores")
public class InstrutorController {
    @Autowired
    private InstrutorRepository repository;

    @PostMapping
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DadosDetalhamentoInstrutor>
    cadastrarInstrutor(
            @RequestBody @Valid
            DadosCadastroInstrutor dados,

            UriComponentsBuilder uriBuilder
    ) {
        if (repository.existsByEmail(dados.email())) {
            throw new ValidacaoException(
                    "Já existe um instrutor com esse e-mail!"
            );
        }

        if (repository.existsByCnh(dados.cnh())) {
            throw new ValidacaoException(
                    "Já existe um instrutor com essa CNH!"
            );
        }

        Instrutor instrutor = new Instrutor(dados);

        repository.save(instrutor);

        DadosDetalhamentoInstrutor dto =
                new DadosDetalhamentoInstrutor(instrutor);

        URI uri = uriBuilder
                .path("/instrutores/{id}")
                .buildAndExpand(dto.id())
                .toUri();

        return ResponseEntity
                .created(uri)
                .body(dto);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<DadosListagemInstrutor>>
    listarInstrutores(

            @PageableDefault(
                    size = 10,
                    sort = "nome",
                    direction = ASC
            )
            Pageable paginacao
    ) {
        Page<DadosListagemInstrutor> pagina =
                repository
                        .findAllByAtivoTrue(paginacao)
                        .map(DadosListagemInstrutor::new);

        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DadosDetalhamentoInstrutor> detalharInstrutor(@PathVariable Long id) {
        Instrutor instrutor = repository
                .findById(id)
                .orElseThrow(() ->
                        new InstrutorNotFoundException("ID do instrutor informado não existe!"));
        return ResponseEntity.ok(new DadosDetalhamentoInstrutor(instrutor));
    }

    @PutMapping
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DadosDetalhamentoInstrutor> atualizarInstrutor(@RequestBody @Valid DadosAtualizacaoInstrutor dados) {
        Instrutor instrutor = repository
                .findById(dados.id())
                .orElseThrow(() ->
                        new InstrutorNotFoundException("ID do instrutor informado não existe!"));
        instrutor.atualizarInformacoes(dados);
        repository.save(instrutor);
        return ResponseEntity.ok(new DadosDetalhamentoInstrutor(instrutor));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluirInstrutor(@PathVariable Long id) {
        Instrutor instrutor = repository
                .findById(id)
                .orElseThrow(() ->
                        new InstrutorNotFoundException("ID do instrutor informado não existe!"));
        instrutor.excluir();
        repository.save(instrutor);
        return ResponseEntity.noContent().build();
    }
}