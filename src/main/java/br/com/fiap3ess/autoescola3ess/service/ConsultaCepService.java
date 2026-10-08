package br.com.fiap3ess.autoescola3ess.service;

import br.com.fiap3ess.autoescola3ess.domain.agenda.ValidacaoException;
import br.com.fiap3ess.autoescola3ess.domain.endereco.DadosConsultaCep;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class ConsultaCepService {
    private final RestClient client;

    public ConsultaCepService(@Qualifier("viaCepRestClient") RestClient client) {
        this.client = client;
    }

    public DadosConsultaCep consultar(String cep) {
        if (cep == null || !cep.matches("[0-9]{5}-?[0-9]{3}")) {
            throw new ValidacaoException("Informe um CEP com 8 números, com ou sem hífen!");
        }
        ViaCepResposta resposta;
        try {
            resposta = client.get().uri("/{cep}/json/", cep.replace("-", ""))
                    .retrieve().body(ViaCepResposta.class);
        } catch (RestClientException exception) {
            throw new ServicoExternoException();
        }
        if (resposta == null) {
            throw new ServicoExternoException();
        }
        if (Boolean.TRUE.equals(resposta.erro())) {
            throw new CepNaoEncontradoException();
        }
        if (resposta.cep() == null || resposta.localidade() == null || resposta.uf() == null) {
            throw new ServicoExternoException();
        }
        return new DadosConsultaCep(resposta.cep(), resposta.logradouro(), resposta.complemento(),
                resposta.bairro(), resposta.localidade(), resposta.uf(), resposta.ibge());
    }

    // O ViaCEP chama a cidade de "localidade"; o contrato da autoescola usa "cidade".
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    public record ViaCepResposta(String cep, String logradouro, String complemento,
                                String bairro, String localidade, String uf, String ibge, Boolean erro) { }
}
