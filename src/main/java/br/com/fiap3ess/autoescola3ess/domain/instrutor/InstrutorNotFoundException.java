package br.com.fiap3ess.autoescola3ess.domain.instrutor;

public class InstrutorNotFoundException extends RuntimeException {
    public InstrutorNotFoundException(String message) {
        super(message);
    }
}