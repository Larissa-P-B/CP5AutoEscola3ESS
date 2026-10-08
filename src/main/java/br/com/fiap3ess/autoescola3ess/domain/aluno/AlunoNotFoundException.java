package br.com.fiap3ess.autoescola3ess.domain.aluno;

public class AlunoNotFoundException extends RuntimeException {
    public AlunoNotFoundException(String message) {
        super(message);
    }
}