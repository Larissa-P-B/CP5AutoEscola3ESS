# Alterações da atividade — SOA e WebServices

## Consumo de serviço externo

`ViaCepConfig` cria o RestClient com URL e timeouts. `ConsultaCepService` valida o CEP, faz GET no ViaCEP e converte `localidade` em `cidade`. `EnderecoController` expõe GET /enderecos/cep/{cep}, protegido pelo JWT existente. Erros têm respostas 400, 404 e 502, tratadas por GlobalExceptionHandler.

## Swagger

O pom.xml inclui springdoc-openapi-starter-webmvc-ui 3.0.3, compatível com Spring Boot 4. SwaggerConfig registra informações da API e o esquema Bearer. Controllers recebem tags. O login possui segurança vazia no OpenAPI. SecurityConfig permite abrir o Swagger sem login, mantendo as rotas de negócio autenticadas.

## CORS

CorsConfig define origens configuráveis, métodos e cabeçalhos permitidos. SecurityConfig habilita o CORS na cadeia de filtros. Testes verificam OPTIONS sem JWT e rejeição de origens não autorizadas.

## Testes

As quatro entidades têm testes de API com MockMvc, banco H2 e transações revertidas. Os testes do ViaCEP usam MockRestServiceServer e não chamam a internet. Há testes adicionais para login, JWT, documentação, CORS e validadores. O workflow GitHub Actions executa Maven verify com Java 25.

## Complementos encontrados na revisão

- Expiração JWT corrigida para Instant.now() + 30 minutos, independente do fuso do servidor.
- Senha MySQL e segredo JWT movidos para variáveis de ambiente obrigatórias.
- Mantidas migrations V1 a V10 para preservar os checksums de bancos existentes.
- Consulta paginada, detalhamento e atualização de instruções adicionados, pois o anexo só possuía criação e cancelamento.
- Atualização reaplica os validadores e exclui a própria instrução das consultas de conflito e limite diário.
- Exigência de 24h para reagendar o horário original documentada como política adicionada, sujeita ao documento de regras do professor.
- Validação de hora inteira também rejeita segundos/nanos.
- Mantida a credencial acadêmica do administrador descrita no README original.

## Referências técnicas

- https://viacep.com.br/
- https://springdoc.org/
- https://docs.spring.io/spring-framework/reference/integration/rest-clients.html

