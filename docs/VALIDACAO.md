# Validação da entrega
Comando de verificação: `./mvnw -B clean verify` com JDK 25. Resultado: **BUILD SUCCESS**; JAR gerado com sucesso.

## Testes

**57 testes executados, 0 falhas, 0 erros, 0 ignorados.**

| Classe | Testes | Falhas | Erros |
| --- | --- | --- | --- |
| AlunoApiTest | 5 | 0 | 0 |
| AutoEscola3EssApplicationTests | 1 | 0 | 0 |
| ConsultaCepServiceTest | 14 | 0 | 0 |
| InfraApiTest | 6 | 0 | 0 |
| InstrucaoApiTest | 11 | 0 | 0 |
| InstrutorApiTest | 5 | 0 | 0 |
| UsuarioApiTest | 5 | 0 | 0 |
| ValidadoresAgendamentoTest | 10 | 0 | 0 |

Os testes de API usam H2 em memória com o perfil test. Os testes ViaCEP simulam o contrato HTTP com MockRestServiceServer. O teste de login usa BCrypt e a geração/validação real do JWT. O hash da migration V9 foi conferido contra a senha acadêmica admin documentada no README.

## Limites da validação

- MySQL real e execução das migrations Flyway não foram testados neste ambiente. As migrations originais V1 a V10 foram preservadas.
- Os testes de integração externa não consultam o ViaCEP real; a chamada real requer internet.

