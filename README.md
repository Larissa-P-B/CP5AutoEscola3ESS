# AutoEscola3ESS

API REST desenvolvida para gerenciar uma autoescola, permitindo o cadastro de alunos, instrutores e usuários, autenticação com JWT, agendamento de instruções e cancelamento conforme regras de negócio.

Projeto desenvolvido para a disciplina **SOA e WebServices**, incluindo os checkpoints anteriores e os requisitos da atividade.


## Requisitos desta atividade

| Demanda | Implementação |
| --- | --- |
| Consumir API/WebService externo | `ConsultaCepService` consome o ViaCEP com RestClient |
| Documentação automática | springdoc-openapi 3.0.3, Swagger UI e Bearer JWT |
| CORS | `CorsConfig`, integrado ao Spring Security e configurável por ambiente |
| Testes por entidade | AlunoApiTest, InstrutorApiTest, UsuarioApiTest e InstrucaoApiTest |
| Recursos anteriores | JWT, perfis, BCrypt, MySQL, Flyway, CRUD e regras de agendamento/cancelamento |

O único anexo recebido foi o código-fonte ZIP. As regras implementadas foram conferidas com os validadores e a documentação existentes; o documento de regras de negócio citado no enunciado não estava no ZIP.

## Integrante

| Nome | RM |
| --- | --- |
| Larissa Pereira Biusse | 564068 |

## Objetivo

O projeto tem como objetivo disponibilizar uma API segura para o gerenciamento das principais operações de uma autoescola. Somente usuários cadastrados podem acessar os recursos protegidos, mediante autenticação com token JWT.

A aplicação implementa:

- CRUD completo de alunos;
- CRUD completo de instrutores;
- CRUD administrativo de usuários;
- Criptografia de senhas com BCrypt;
- Autenticação stateless com JWT;
- Controle de acesso por perfil;
- Alteração da própria senha;
- Agendamento de instruções;
- Seleção automática de instrutor disponível;
- Cancelamento de instruções;
- Validações das regras de negócio;
- Exclusão lógica de alunos e instrutores;
- Versionamento do banco com Flyway.

## Tecnologias utilizadas

- Java 25 ou superior;
- Spring Boot 4.0.5;
- Spring Web MVC;
- Spring Data JPA;
- Spring Security;
- Bean Validation;
- JWT com `java-jwt`;
- BCrypt;
- MySQL;
- Flyway;
- Maven;
- Lombok;
- Postman ou Insomnia para testes.

O `pom.xml` está configurado para Java 25, mantendo Spring Boot 4.0.5.

## Organização do projeto

```text
src/main/java/br/com/fiap3ess/autoescola3ess
├── controller
│   ├── AlunoController.java
│   ├── HealthCheckController.java
│   ├── InstrucaoController.java
│   ├── InstrutorController.java
│   ├── LoginController.java
│   └── UsuarioController.java
├── domain
│   ├── agenda
│   │   └── validacao
│   ├── aluno
│   ├── endereco
│   ├── instrutor
│   └── usuario
├── infra
│   ├── exception
│   └── security
├── service
│   └── AgendaDeInstrucoes.java
└── AutoEscola3EssApplication.java
```

As responsabilidades estão separadas da seguinte forma:

- `controller`: recebe as requisições HTTP e devolve as respostas;
- `domain`: contém entidades, DTOs, enums, repositórios e regras específicas;
- `service`: coordena o agendamento e o cancelamento das instruções;
- `infra/security`: configura autenticação, autorização e JWT;
- `infra/exception`: centraliza o tratamento de erros;
- `db/migration`: contém as migrations executadas pelo Flyway.

## Requisitos para execução

Antes de iniciar, instale:

- JDK 25 ou superior;
- MySQL 8 ou superior;
- Git;
- Postman ou Insomnia.

O Maven Wrapper já está incluído no projeto, portanto não é obrigatório instalar o Maven separadamente.

## Java 25 no IntelliJ

1. Em **File → Project Structure → Project**, selecione o **Project SDK 25** e o **Language level 25**.
2. Em **Settings → Build, Execution, Deployment → Build Tools → Maven → Runner**, selecione o **JRE 25**.
3. Na configuração de execução de **AutoEscola3EssApplication**, selecione o **JRE/JDK 25** e mantenha as variáveis de ambiente existentes.
4. Recarregue o projeto Maven após alterar o `pom.xml`.
5. No terminal, execute `java -version` e `.\mvnw.cmd -version`. Ambos devem indicar Java 25. O Maven Wrapper usa `JAVA_HOME`, quando configurada; confira se ela aponta para o JDK 25.

## Configuração do banco de dados

Crie o banco no MySQL:

```sql
CREATE DATABASE IF NOT EXISTS autoescola3ess
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

A aplicação lê a conexão e a chave JWT de variáveis de ambiente. **Configure as variáveis no mesmo terminal em que executar o projeto.**

No PowerShell:

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "SUA_SENHA_DO_MYSQL"
$env:JWT_SECRET = "SUA_CHAVE_ALEATORIA_LONGA"
# Opcional, se a conexão for diferente da padrão:
$env:DB_URL = "jdbc:mysql://localhost:3306/autoescola3ess"
```

No Linux/macOS:

```bash
export DB_USERNAME='root'
export DB_PASSWORD='SUA_SENHA_DO_MYSQL'
export JWT_SECRET='SUA_CHAVE_ALEATORIA_LONGA'
```

Gere uma chave aleatória para substituir `SUA_CHAVE_ALEATORIA_LONGA`. No PowerShell:

```powershell
$bytes = New-Object byte[] 32
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($bytes)
$env:JWT_SECRET = [Convert]::ToBase64String($bytes)
$rng.Dispose()
```

`DB_PASSWORD` e `JWT_SECRET` são obrigatórias para executar a aplicação normal. O perfil `test` contém valores isolados para os testes. Não coloque senhas pessoais no código ou no GitHub. Se usar uma IDE, configure essas variáveis na configuração de execução. Um arquivo `.env` sozinho **não é carregado automaticamente** pelo Spring Boot.

Execute o projeto numa pasta local, fora do OneDrive, para evitar problemas de sincronização e bloqueio de arquivos.

## Migrations

Ao iniciar a aplicação, o Flyway cria e atualiza automaticamente as tabelas.

| Migration | Finalidade |
| --- | --- |
| V1 | Criação da tabela de instrutores |
| V2 | Inclusão do telefone dos instrutores |
| V3 | Inclusão do status ativo dos instrutores |
| V4 | Criação da tabela de usuários |
| V5 | Inclusão do perfil dos usuários |
| V6 | Criação da tabela de alunos |
| V7 | Criação da tabela de instruções |
| V8 | Inclusão do status ativo dos alunos |
| V9 | Cadastro do administrador inicial |
| V10 | Inclusão dos campos de cancelamento das instruções |

## Como executar

No Windows:

```bash
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

A aplicação ficará disponível em:

```text
http://localhost:8080
```

Para verificar a integridade da API:

```http
GET http://localhost:8080/health-check
Authorization: Bearer SEU_TOKEN
```

## Credenciais iniciais de teste

A migration `V9__insert-admin.sql` cadastra o administrador inicial.

```text
Login: admin
Senha: admin
Perfil: ADMIN
```

> A senha é armazenada no banco somente como hash BCrypt. Essas credenciais são destinadas ao ambiente acadêmico de teste e devem ser alteradas em uma implantação real.

## Autenticação

### Efetuar login

```http
POST /login
```

```json
{
  "login": "admin",
  "senha": "admin"
}
```

Resposta:

```json
{
  "tokenJWT": "eyJhbGciOiJIUzI1NiJ9..."
}
```

Nas rotas protegidas, envie o token no cabeçalho:

```http
Authorization: Bearer SEU_TOKEN_JWT
```

O token possui validade de 30 minutos.

## Perfis de acesso

| Perfil | Permissões |
| --- | --- |
| `ADMIN` | Gerencia usuários e acessa os demais recursos autorizados |
| `USER` | Acessa os recursos autenticados, mas não pode gerenciar usuários |

As operações de cadastro, listagem, atualização de perfil e exclusão de usuários são exclusivas do perfil `ADMIN`.

## Endpoints

### Login

| Método | Endpoint | Acesso | Descrição |
| --- | --- | --- | --- |
| POST | `/login` | Público | Autentica o usuário e gera o JWT |

### Instrutores

| Método | Endpoint | Acesso | Descrição |
| --- | --- | --- | --- |
| POST | `/instrutores` | ADMIN | Cadastra um instrutor |
| GET | `/instrutores` | ADMIN ou USER | Lista os instrutores ativos |
| GET | `/instrutores/{id}` | ADMIN | Detalha um instrutor |
| PUT | `/instrutores` | ADMIN | Atualiza nome, telefone e endereço |
| DELETE | `/instrutores/{id}` | ADMIN | Inativa um instrutor |

A listagem é paginada em 10 registros e ordenada pelo nome em ordem crescente.

Exemplo de cadastro:

```json
{
  "nome": "Vanessa Maria Lima",
  "email": "vanessa.instrutor@autoescola.com",
  "telefone": "11988887777",
  "cnh": "12345678901",
  "especialidade": "CARROS",
  "endereco": {
    "logradouro": "Rua dos Instrutores",
    "numero": "150",
    "complemento": "Sala 2",
    "bairro": "Ipiranga",
    "cidade": "São Paulo",
    "uf": "SP",
    "cep": "04280-000"
  }
}
```

Especialidades permitidas:

```text
MOTOS
CARROS
VANS
CAMINHOES
```

Na atualização, e-mail, CNH e especialidade não podem ser alterados. A exclusão é lógica: o registro permanece no banco com `ativo = false`.

### Alunos

| Método | Endpoint | Acesso | Descrição |
| --- | --- | --- | --- |
| POST | `/alunos` | ADMIN | Cadastra um aluno |
| GET | `/alunos` | ADMIN ou USER | Lista os alunos ativos |
| GET | `/alunos/{id}` | ADMIN ou USER | Detalha um aluno |
| PUT | `/alunos` | ADMIN | Atualiza nome, telefone e endereço |
| DELETE | `/alunos/{id}` | ADMIN | Inativa um aluno |

A listagem é paginada em 10 registros e ordenada pelo nome em ordem crescente.

Exemplo de cadastro:

```json
{
  "nome": "Mariana Souza",
  "email": "mariana.aluna@email.com",
  "telefone": "11999998888",
  "cpf": "98765432100",
  "endereco": {
    "logradouro": "Avenida Nazaré",
    "numero": "500",
    "complemento": "Apartamento 12",
    "bairro": "Ipiranga",
    "cidade": "São Paulo",
    "uf": "SP",
    "cep": "04263-000"
  }
}
```

Na atualização, e-mail e CPF não podem ser alterados. A exclusão é lógica: o registro permanece no banco com `ativo = false`.

### Usuários

| Método | Endpoint | Acesso | Descrição |
| --- | --- | --- | --- |
| POST | `/usuarios` | ADMIN | Cadastra um usuário com senha BCrypt |
| GET | `/usuarios` | ADMIN | Lista os usuários |
| GET | `/usuarios/{id}` | ADMIN | Detalha um usuário |
| PUT | `/usuarios/{id}/perfil` | ADMIN | Atualiza o perfil do usuário |
| DELETE | `/usuarios/{id}` | ADMIN | Exclui um usuário |
| PATCH | `/usuarios/minha-senha` | Autenticado | Altera a senha do próprio usuário |

Exemplo de cadastro:

```json
{
  "login": "jessica",
  "senha": "senha123",
  "perfil": "USER"
}
```

Exemplo de alteração de perfil:

```json
{
  "perfil": "ADMIN"
}
```

Exemplo de alteração da própria senha:

```json
{
  "senhaAtual": "senha123",
  "novaSenha": "jessica456"
}
```

A senha explícita nunca é retornada pela API e nunca é salva diretamente no banco.

### Instruções

| Método | Endpoint | Acesso | Descrição |
| --- | --- | --- | --- |
| POST | `/instrucoes` | Autenticado | Agenda uma instrução |
| GET | `/instrucoes` | Autenticado | Lista instruções com paginação e ordenação |
| GET | `/instrucoes/{id}` | Autenticado | Detalha uma instrução |
| PUT | `/instrucoes/{id}` | Autenticado | Reagenda uma instrução |
| PATCH | `/instrucoes/{id}/cancelamento` | ADMIN ou USER | Cancela uma instrução |

Exemplo de agendamento com instrutor escolhido:

```json
{
  "id_aluno": 1,
  "id_instrutor": 1,
  "data_hora": "13/10/2026 - 10:00"
}
```

Exemplo de agendamento com escolha automática de instrutor:

```json
{
  "id_aluno": 1,
  "especialidade": "CARROS",
  "data_hora": "13/10/2026 - 10:00"
}
```

Use uma data futura de segunda a sábado. Os exemplos de data devem ser ajustados ao dia em que a API for executada.

Quando `id_instrutor` não é informado, a especialidade é obrigatória. A API seleciona aleatoriamente um instrutor ativo, da especialidade informada e disponível no horário.

Exemplo de cancelamento:

```json
{
  "motivo": "ALUNO_DESISTIU"
}
```

Motivos permitidos:

```text
ALUNO_DESISTIU
INSTRUTOR_CANCELOU
OUTROS
```

### Consulta externa de endereço

```http
GET /enderecos/cep/01001000
Authorization: Bearer SEU_TOKEN_JWT
```

Resposta esperada para esse CEP:

```json
{
  "cep": "01001-000",
  "logradouro": "Praça da Sé",
  "complemento": "lado ímpar",
  "bairro": "Sé",
  "cidade": "São Paulo",
  "uf": "SP",
  "ibge": "3550308"
}
```

O campo `cidade` é mapeado de `localidade` do ViaCEP. Aceita CEP com ou sem hífen. A consulta auxilia o preenchimento do endereço de alunos/instrutores; o cadastro continua recebendo o endereço completo. O número do imóvel deve ser informado pelo usuário.

- `400`: CEP fora do formato esperado;
- `404`: CEP com formato válido, mas inexistente;
- `502`: falha de rede, timeout ou resposta inválida do serviço externo.

Há timeout de conexão de 3 segundos e de leitura de 5 segundos. O consumo real do ViaCEP exige internet. Os testes usam respostas simuladas, inclusive `erro: true` e `erro: "true"`.

### Atualização de instrução

```http
PUT /instrucoes/1
```

Use o mesmo JSON do agendamento, com `id_aluno`, `id_instrutor` e `data_hora` obrigatórios. Na atualização, o instrutor é informado explicitamente. A instrução atual é excluída das consultas de conflito e limite diário, evitando conflito consigo mesma. Canceladas não podem ser atualizadas.

A política adotada para a nova rota exige 24 horas de antecedência do horário original, evitando contornar o prazo de cancelamento por um reagendamento. Para o novo horário, aplicam-se as regras existentes de agendamento. Essa política de atualização foi adicionada ao projeto; deve ser comparada com o documento do professor, caso ele determine outro comportamento.

O cancelamento é a operação de exclusão lógica da instrução. Não há DELETE físico, para preservar o histórico.

## Swagger e OpenAPI

Com a aplicação iniciada, abra:

- Interface: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- Contrato JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

1. Abra o grupo **Autenticação** e execute `POST /login` com `admin`/`admin`.
2. Copie apenas o valor de `tokenJWT`.
3. Clique em **Authorize**, cole o token sem o prefixo `Bearer` e confirme.
4. Execute os recursos protegidos. O Swagger enviará o cabeçalho Bearer.

A interface e o contrato OpenAPI são públicos; as operações de negócio continuam protegidas por JWT. As entidades são organizadas por tags e o login é documentado sem exigir token.

## CORS

Por padrão, os navegadores podem chamar a API a partir de `http://localhost:3000` e `http://localhost:5173`. Para mudar:

```powershell
$env:CORS_ALLOWED_ORIGINS = "http://localhost:3000,https://seu-frontend.example"
```

As origens são exatas, separadas por vírgula e sem barra final. Métodos: GET, POST, PUT, PATCH, DELETE e OPTIONS. Cabeçalhos aceitos: Authorization, Content-Type e Accept. O cabeçalho Location fica visível ao frontend. Não são usados cookies; o frontend envia JWT em Authorization. O preflight é processado pelo CORS antes da autenticação.

CORS é uma regra do navegador e não substitui autenticação. Postman não depende dessa permissão.

## Regras de negócio do agendamento

- A autoescola funciona de segunda-feira a sábado;
- O horário de funcionamento é das 06:00 às 21:00;
- Cada instrução possui duração fixa de uma hora;
- Os agendamentos devem utilizar horas inteiras;
- O agendamento exige antecedência mínima de 30 minutos;
- Alunos inativos não podem participar de novas instruções;
- Instrutores inativos não podem receber novas instruções;
- Um aluno pode realizar no máximo duas instruções no mesmo dia;
- Um instrutor não pode possuir duas instruções agendadas na mesma data e horário;
- Instruções canceladas não ocupam o horário do instrutor;
- Instruções canceladas não entram no limite diário do aluno;
- A escolha do instrutor é opcional;
- Quando o instrutor não é informado, a especialidade passa a ser obrigatória.

## Regras de cancelamento

- O motivo é obrigatório;
- São aceitos somente os motivos `ALUNO_DESISTIU`, `INSTRUTOR_CANCELOU` e `OUTROS`;
- O cancelamento exige antecedência mínima de 24 horas;
- Uma instrução já cancelada não pode ser cancelada novamente;
- A instrução não é apagada do banco: seu status passa de `AGENDADA` para `CANCELADA`;
- A data e o motivo do cancelamento ficam registrados.

## Códigos de resposta

| Código | Significado |
| --- | --- |
| 200 | Requisição executada com sucesso |
| 201 | Registro criado com sucesso |
| 204 | Operação concluída sem corpo de resposta |
| 400 | JSON, validação ou regra de negócio inválida |
| 401 | Usuário não autenticado ou token inválido |
| 403 | Usuário autenticado sem permissão para a operação |
| 404 | Registro ou CEP não encontrado |
| 502 | Falha ao consumir o ViaCEP |

## Exemplos de validações

A API recusa, entre outras situações:

- Campos obrigatórios ausentes;
- E-mails com formato inválido;
- E-mail, CPF, CNH ou login duplicados;
- Agendamento aos domingos;
- Agendamento fora do horário de funcionamento;
- Agendamento com menos de 30 minutos de antecedência;
- Terceira instrução do mesmo aluno no mesmo dia;
- Conflito de horário do instrutor;
- Agendamento envolvendo aluno ou instrutor inativo;
- Cancelamento com menos de 24 horas de antecedência;
- Cancelamento sem motivo ou com motivo inválido;
- Usuário `USER` tentando acessar o gerenciamento de usuários.

## Testes automatizados

No PowerShell:

```powershell
.\mvnw.cmd clean verify
```

No Linux/macOS:

```bash
./mvnw clean verify
```

O comando compila, executa os testes e gera o JAR. Não precisa de MySQL, variáveis de ambiente de produção nem internet para chamar o ViaCEP. A primeira execução precisa baixar as dependências do Maven. Os testes de API utilizam Spring Boot + MockMvc + H2 em memória. Cada teste de API roda em transação revertida ao final. Flyway fica desativado apenas no perfil de teste.

| Classe | Exemplos cobertos |
| --- | --- |
| AlunoApiTest | Cadastro, consulta, listagem, atualização, exclusão lógica, CPF duplicado, dados inválidos e 404 |
| InstrutorApiTest | Cadastro, consulta, listagem, atualização, exclusão lógica, CNH duplicada, dados inválidos e 404 |
| UsuarioApiTest | Cadastro com BCrypt, consulta sem senha, perfil, exclusão, login duplicado, troca de senha e restrição ADMIN |
| InstrucaoApiTest | Agendamento, consulta, atualização, cancelamento, seleção automática, conflito, limite diário e inativos |
| ConsultaCepServiceTest | Contrato HTTP simulado, CEP inválido/inexistente, timeout, indisponibilidade, corpo inválido e códigos HTTP |
| InfraApiTest | Login/JWT, token inválido, Swagger público e preflight CORS permitido/bloqueado |
| ValidadoresAgendamentoTest | Domingo, funcionamento, horas inteiras e antecedência |
| AutoEscola3EssApplicationTests | Inicialização do contexto com H2 |

Os relatórios Maven são gerados em `target/surefire-reports/`. Os testes em H2 não substituem a conferência das migrations e da persistência no MySQL real. Veja `docs/VALIDACAO.md` para o resultado da verificação desta entrega.


## Segurança

- A API utiliza sessão stateless;
- As senhas são codificadas com BCrypt;
- O JWT identifica o usuário autenticado;
- `/login`, Swagger UI e contrato OpenAPI são públicos;
- As demais rotas exigem token;
- O gerenciamento de usuários é exclusivo de administradores;
- A alteração de senha utiliza a identidade contida no token, impedindo que um usuário altere a senha de outro.



## Conclusão

O projeto `AutoEscola3ESS` reúne os recursos anteriores e acrescenta consumo do ViaCEP, documentação automática Swagger, CORS e exemplos de testes automatizados por entidade. A solução oferece operações completas para alunos, instrutores e usuários, com autenticação JWT, autorização baseada em perfis e proteção das senhas por BCrypt.

O processo de agendamento aplica as regras de funcionamento da autoescola, disponibilidade dos participantes, antecedência e limite diário. O cancelamento mantém o histórico da instrução e registra o motivo, a data e o novo status, sem remover os dados do banco.

A organização em controllers, serviços, entidades, DTOs, repositórios, validadores e infraestrutura de segurança facilita a manutenção e demonstra a aplicação de conceitos de APIs REST, arquitetura em camadas, persistência, segurança e regras de negócio.
