package br.com.fiap3ess.autoescola3ess.controller;

import br.com.fiap3ess.autoescola3ess.domain.endereco.DadosConsultaCep;
import br.com.fiap3ess.autoescola3ess.service.ConsultaCepService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/enderecos")
@Tag(name = "Endereços", description = "Consumo do webservice externo ViaCEP")
public class EnderecoController {
    private final ConsultaCepService service;
    public EnderecoController(ConsultaCepService service) { this.service = service; }

    @GetMapping("/cep/{cep}")
    @Operation(summary = "Consultar endereço pelo CEP", description = "Aceita 01001000 ou 01001-000. Exige JWT.")
    @ApiResponse(responseCode = "200", description = "Endereço encontrado")
    @ApiResponse(responseCode = "400", description = "Formato de CEP inválido")
    @ApiResponse(responseCode = "404", description = "CEP inexistente")
    @ApiResponse(responseCode = "502", description = "Falha ou resposta inválida do ViaCEP")
    public DadosConsultaCep consultar(@PathVariable String cep) { return service.consultar(cep); }
}
