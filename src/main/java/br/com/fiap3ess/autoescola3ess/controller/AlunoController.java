package br.com.fiap3ess.autoescola3ess.controller;

import br.com.fiap3ess.autoescola3ess.domain.agenda.ValidacaoException;
import br.com.fiap3ess.autoescola3ess.domain.aluno.*;
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

@io.swagger.v3.oas.annotations.tags.Tag(name = "Alunos")
@RestController
@RequestMapping("/alunos")
public class AlunoController {

    @Autowired
    private AlunoRepository repository;

    @PostMapping
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DadosDetalhamentoAluno> cadastrar(
            @RequestBody @Valid DadosCadastroAluno dados,
            UriComponentsBuilder uriBuilder
    ) {
        if (repository.existsByEmail(dados.email())) {
            throw new ValidacaoException("Já existe um aluno com esse e-mail!");
        }

        if (repository.existsByCpf(dados.cpf())) {
            throw new ValidacaoException("Já existe um aluno com esse CPF!");
        }

        Aluno aluno = new Aluno(dados);
        repository.save(aluno);

        URI uri = uriBuilder
                .path("/alunos/{id}")
                .buildAndExpand(aluno.getId())
                .toUri();

        return ResponseEntity
                .created(uri)
                .body(new DadosDetalhamentoAluno(aluno));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<DadosListagemAluno>> listar(
            @PageableDefault(
                    size = 10,
                    sort = "nome",
                    direction = ASC
            )
            Pageable paginacao
    ) {
        Page<DadosListagemAluno> pagina = repository
                .findAllByAtivoTrue(paginacao)
                .map(DadosListagemAluno::new);

        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<DadosDetalhamentoAluno> detalhar(
            @PathVariable Long id
    ) {
        Aluno aluno = repository.findById(id)
                .orElseThrow(() ->
                        new AlunoNotFoundException(
                                "ID do aluno informado não existe!"
                        )
                );

        return ResponseEntity.ok(
                new DadosDetalhamentoAluno(aluno)
        );
    }

    @PutMapping
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DadosDetalhamentoAluno> atualizar(
            @RequestBody @Valid DadosAtualizacaoAluno dados
    ) {
        Aluno aluno = repository.findById(dados.id())
                .orElseThrow(() ->
                        new AlunoNotFoundException(
                                "ID do aluno informado não existe!"
                        )
                );

        aluno.atualizarInformacoes(dados);

        return ResponseEntity.ok(
                new DadosDetalhamentoAluno(aluno)
        );
    }

    @DeleteMapping("/{id}")
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id
    ) {
        Aluno aluno = repository.findById(id)
                .orElseThrow(() ->
                        new AlunoNotFoundException(
                                "ID do aluno informado não existe!"
                        )
                );

        aluno.excluir();

        return ResponseEntity.noContent().build();
    }
}