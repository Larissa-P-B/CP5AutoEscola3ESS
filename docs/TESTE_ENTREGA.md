# Roteiro de teste da entrega — Java 25

## 1. Conferir o Java e executar os testes automatizados

Extraia o ZIP para uma pasta local, fora do OneDrive. Abra a pasta que contém pom.xml no IntelliJ. Configure Project SDK, Language level, Maven Runner e JRE da aplicação para 25. Recarregue o Maven.

No PowerShell, na pasta que contém pom.xml:

```powershell
java -version
.\mvnw.cmd -version
.\mvnw.cmd clean verify
```

Os dois primeiros comandos devem indicar Java 25. A verificação deve concluir com BUILD SUCCESS e com testes executados, sem falhas ou erros. Não utilize -DskipTests.

Os testes de AlunoApiTest, InstrutorApiTest, UsuarioApiTest e InstrucaoApiTest verificam as quatro entidades. Os demais testes verificam segurança, Swagger, CORS, integração ViaCEP e regras de negócio. O perfil test utiliza H2 em memória, sem modificar o MySQL, e simula o ViaCEP. A primeira execução precisa de internet para baixar as dependências Maven.

## 2. Iniciar com MySQL

Mantenha seu banco autoescola3ess e configure as variáveis na execução da aplicação no IntelliJ:

- DB_USERNAME: usuário do MySQL;
- DB_PASSWORD: senha do MySQL;
- JWT_SECRET: chave local para assinar os tokens.

Para executar pelo terminal, configure as variáveis no mesmo PowerShell:

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "SUA_SENHA_DO_MYSQL"
$env:JWT_SECRET = "chave-local-autoescola-para-testes-2026"
.\mvnw.cmd spring-boot:run
```

A chave acima é apenas um exemplo acadêmico local. O arquivo .env não é carregado automaticamente. Aguarde Started AutoEscola3EssApplication e mantenha a aplicação executando.

## 3. Swagger e autenticação

Abra http://localhost:8080/swagger-ui.html. A interface deve abrir sem token. Confira os grupos das entidades, Endereços e Autenticação. Execute POST /login com:

```json
{"login":"admin","senha":"admin"}
```

A credencial é a inicial de teste; se você já alterou a senha, use a atual. Espere 200 e tokenJWT. Clique em Authorize e cole somente o token, sem escrever Bearer. Execute GET /alunos; com um perfil autorizado deve retornar 200.

Abra também http://localhost:8080/v3/api-docs para conferir o contrato OpenAPI automático.

## 4. Consumo real do ViaCEP

No Swagger autorizado, execute GET /enderecos/cep/{cep} com 01001000. Espere 200 e endereço com cidade São Paulo. Repita com 01001-000; ambos os formatos são aceitos.

- CEP 123: espere 400;
- CEP 99999999: espere 404, conforme o contrato de CEP inexistente do ViaCEP;
- Sem autorização JWT: espere 401.

A chamada real exige internet. Falha, timeout ou resposta inválida do provedor retornam 502. Os testes automatizados também cobrem essas situações usando respostas simuladas.

## 5. CORS

Com a aplicação em execução, abra outro PowerShell:

```powershell
curl.exe -i -X OPTIONS "http://localhost:8080/alunos" -H "Origin: http://localhost:3000" -H "Access-Control-Request-Method: POST" -H "Access-Control-Request-Headers: Authorization,Content-Type"
```

Espere 200 e Access-Control-Allow-Origin: http://localhost:3000. O preflight não exige JWT.

```powershell
curl.exe -i -X OPTIONS "http://localhost:8080/alunos" -H "Origin: https://nao-permitido.example" -H "Access-Control-Request-Method: POST"
```

Espere 403, sem um cabeçalho que autorize essa origem. Os comandos verificam a resposta ao preflight CORS; o Swagger na mesma origem, sozinho, não comprova o CORS entre origens diferentes.

## 6. Conferência da entrega

| Requisito | Evidência |
| --- | --- |
| API externa | Resposta real de GET /enderecos/cep/01001000 |
| Swagger | Interface acessível, OpenAPI e execução com Bearer JWT |
| CORS | Preflight permitido 200 e origem não autorizada 403 |
| Testes por entidade | Classes das quatro entidades e resumo Maven sem falhas |

