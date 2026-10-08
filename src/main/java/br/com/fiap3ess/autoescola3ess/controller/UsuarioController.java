package br.com.fiap3ess.autoescola3ess.controller;

import br.com.fiap3ess.autoescola3ess.domain.agenda.ValidacaoException;
import br.com.fiap3ess.autoescola3ess.domain.usuario.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


import java.net.URI;

import static org.springframework.data.domain.Sort.Direction.ASC;

@io.swagger.v3.oas.annotations.tags.Tag(name = "Usuários")
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DadosListagemUsuario> cadastrar(
            @RequestBody @Valid DadosCadastroUsuario dados,
            UriComponentsBuilder uriBuilder
    ) {
        if (repository.existsByLogin(dados.login())) {
            throw new ValidacaoException(
                    "Já existe um usuário com esse login!"
            );
        }

        String senhaCriptografada =
                passwordEncoder.encode(dados.senha());

        Usuario usuario = new Usuario(
                dados.login(),
                senhaCriptografada,
                dados.perfil()
        );

        repository.save(usuario);

        URI uri = uriBuilder
                .path("/usuarios/{id}")
                .buildAndExpand(usuario.getId())
                .toUri();

        return ResponseEntity
                .created(uri)
                .body(new DadosListagemUsuario(usuario));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<DadosListagemUsuario>> listar(
            @PageableDefault(
                    size = 10,
                    sort = "login",
                    direction = ASC
            )
            Pageable paginacao
    ) {
        Page<DadosListagemUsuario> pagina = repository
                .findAll(paginacao)
                .map(DadosListagemUsuario::new);

        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DadosListagemUsuario> detalhar(
            @PathVariable Long id
    ) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        return ResponseEntity.ok(
                new DadosListagemUsuario(usuario)
        );
    }

    @PutMapping("/{id}/perfil")
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DadosListagemUsuario> atualizarPerfil(
            @PathVariable Long id,
            @RequestBody @Valid DadosAtualizacaoPerfil dados
    ) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        usuario.alterarPerfil(dados.perfil());

        return ResponseEntity.ok(
                new DadosListagemUsuario(usuario)
        );
    }

    @DeleteMapping("/{id}")
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id
    ) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        repository.delete(usuario);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/minha-senha")
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> alterarMinhaSenha(
            @RequestBody @Valid DadosAlteracaoSenha dados,
            Authentication authentication
    ) {
        String loginUsuarioAutenticado = authentication.getName();

        Usuario usuario = repository.findByLogin(
                loginUsuarioAutenticado
        );

        boolean senhaAtualCorreta = passwordEncoder.matches(
                dados.senhaAtual(),
                usuario.getPassword()
        );

        if (!senhaAtualCorreta) {
            throw new ValidacaoException(
                    "A senha atual está incorreta!"
            );
        }

        boolean novaSenhaIgualAtual = passwordEncoder.matches(
                dados.novaSenha(),
                usuario.getPassword()
        );

        if (novaSenhaIgualAtual) {
            throw new ValidacaoException(
                    "A nova senha deve ser diferente da senha atual!"
            );
        }

        String novaSenhaCriptografada =
                passwordEncoder.encode(dados.novaSenha());

        usuario.alterarSenha(novaSenhaCriptografada);

        return ResponseEntity.noContent().build();
    }
}
