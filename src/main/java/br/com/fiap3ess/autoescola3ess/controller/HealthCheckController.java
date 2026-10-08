package br.com.fiap3ess.autoescola3ess.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@io.swagger.v3.oas.annotations.tags.Tag(name = "Diagnóstico")
@RestController
@RequestMapping("/health-check")
public class HealthCheckController {
    @GetMapping
    public String healthCheck() {
        return "Verificação de integridade da API da Auto Escola 3ESS";
    }
}