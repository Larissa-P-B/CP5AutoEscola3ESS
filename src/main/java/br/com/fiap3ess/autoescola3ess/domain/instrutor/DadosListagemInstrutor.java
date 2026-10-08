package br.com.fiap3ess.autoescola3ess.domain.instrutor;

public record DadosListagemInstrutor(
        Long id,
        String nome,
        String email,
        String cnh,
        Especialidade especialidade
) {

    public DadosListagemInstrutor(
            Instrutor instrutor
    ) {
        this(
                instrutor.getId(),
                instrutor.getNome(),
                instrutor.getEmail(),
                instrutor.getCnh(),
                instrutor.getEspecialidade()
        );
    }
}