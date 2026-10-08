package br.com.fiap3ess.autoescola3ess.controller;

import br.com.fiap3ess.autoescola3ess.domain.usuario.DadosLogin;
import br.com.fiap3ess.autoescola3ess.domain.usuario.Usuario;
import br.com.fiap3ess.autoescola3ess.infra.security.DadosTokenJWT;
import br.com.fiap3ess.autoescola3ess.infra.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@io.swagger.v3.oas.annotations.tags.Tag(name = "Autenticação")
@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @io.swagger.v3.oas.annotations.security.SecurityRequirements
    @io.swagger.v3.oas.annotations.Operation(summary = "Autenticar e obter token JWT", security = {})
    @PostMapping
    public ResponseEntity<DadosTokenJWT> efetuarLogin(@RequestBody @Valid DadosLogin dados) {
        var token = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());
        Authentication authentication = manager.authenticate(token);
        String tokenJWT = tokenService.generateToken((Usuario) authentication.getPrincipal());
        return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
    }
}