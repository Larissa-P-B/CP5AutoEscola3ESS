package br.com.fiap3ess.autoescola3ess.service;

public class CepNaoEncontradoException extends RuntimeException {
    public CepNaoEncontradoException() { super("CEP não encontrado!"); }
}
