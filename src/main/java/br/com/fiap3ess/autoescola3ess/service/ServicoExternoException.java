package br.com.fiap3ess.autoescola3ess.service;

public class ServicoExternoException extends RuntimeException {
    public ServicoExternoException() { super("Não foi possível consultar o ViaCEP. Tente novamente mais tarde."); }
}
