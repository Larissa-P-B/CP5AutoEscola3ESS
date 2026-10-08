package br.com.fiap3ess.autoescola3ess;

import br.com.fiap3ess.autoescola3ess.service.*;
import br.com.fiap3ess.autoescola3ess.domain.agenda.ValidacaoException;
import br.com.fiap3ess.autoescola3ess.controller.EnderecoController;
import br.com.fiap3ess.autoescola3ess.infra.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ConsultaCepServiceTest {
    private MockRestServiceServer server;
    private ConsultaCepService service;
    @BeforeEach void configurar() {
        var builder = RestClient.builder().baseUrl("https://viacep.com.br/ws");
        server = MockRestServiceServer.bindTo(builder).build();
        service = new ConsultaCepService(builder.build());
    }
    @ParameterizedTest @ValueSource(strings = {"01001000", "01001-000"})
    void consultarENormalizarResposta(String cep) {
        server.expect(requestTo("https://viacep.com.br/ws/01001000/json/"))
                .andRespond(withSuccess("{\"cep\":\"01001-000\",\"logradouro\":\"Praça da Sé\",\"bairro\":\"Sé\",\"localidade\":\"São Paulo\",\"uf\":\"SP\",\"ibge\":\"3550308\",\"ddd\":\"11\",\"regiao\":\"Sudeste\"}", MediaType.APPLICATION_JSON));
        var resposta = service.consultar(cep);
        assertThat(resposta.cidade()).isEqualTo("São Paulo");
        assertThat(resposta.logradouro()).isEqualTo("Praça da Sé");
        server.verify();
    }
    @ParameterizedTest @ValueSource(strings = {"123", "abcdefgh", "01001x000", "01001--000"})
    void rejeitarFormatoInvalidoSemChamarApi(String cep) {
        assertThatThrownBy(() -> service.consultar(cep)).isInstanceOf(ValidacaoException.class);
        server.verify();
    }
    @ParameterizedTest @ValueSource(strings = {"{\"erro\":true}", "{\"erro\":\"true\"}"})
    void tratarCepInexistente(String corpo) {
        server.expect(anything()).andRespond(withSuccess(corpo, MediaType.APPLICATION_JSON));
        assertThatThrownBy(() -> service.consultar("99999999")).isInstanceOf(CepNaoEncontradoException.class);
        server.verify();
    }
    @Test void tratarFalhaDoServico() {
        server.expect(anything()).andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));
        assertThatThrownBy(() -> service.consultar("01001000")).isInstanceOf(ServicoExternoException.class);
        server.verify();
    }
    @Test void tratarTimeout() {
        server.expect(anything()).andRespond(withException(new java.net.SocketTimeoutException("timeout")));
        assertThatThrownBy(() -> service.consultar("01001000")).isInstanceOf(ServicoExternoException.class);
        server.verify();
    }
    @ParameterizedTest @ValueSource(strings = {"", "{}", "nao-json"})
    void tratarRespostaInvalida(String corpo) {
        server.expect(anything()).andRespond(withSuccess(corpo, MediaType.APPLICATION_JSON));
        assertThatThrownBy(() -> service.consultar("01001000")).isInstanceOf(ServicoExternoException.class);
        server.verify();
    }
    @Test void endpointMapeiaErrosParaHttp() throws Exception {
        var mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(new EnderecoController(service))
                .setControllerAdvice(new GlobalExceptionHandler()).build();
        mvc.perform(get("/enderecos/cep/123")).andExpect(status().isBadRequest());
        server.expect(anything()).andRespond(withSuccess("{\"erro\":true}", MediaType.APPLICATION_JSON));
        mvc.perform(get("/enderecos/cep/99999999")).andExpect(status().isNotFound());
        server.verify(); server.reset();
        server.expect(anything()).andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        mvc.perform(get("/enderecos/cep/01001000")).andExpect(status().isBadGateway());
        server.verify();
    }
}
