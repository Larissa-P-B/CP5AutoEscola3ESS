package br.com.fiap3ess.autoescola3ess.domain.endereco;

public record DadosConsultaCep(String cep, String logradouro, String complemento,
                               String bairro, String cidade, String uf, String ibge) { }
