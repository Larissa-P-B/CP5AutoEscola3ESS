package br.com.fiap3ess.autoescola3ess;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.*;

import br.com.fiap3ess.autoescola3ess.domain.usuario.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class InfraApiTest extends ApiTestBase {
    @Test void swaggerDisponivelSemTokenComEsquemaBearer() throws Exception {
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
                .andExpect(jsonPath("$.components.securitySchemes.bearer-key.scheme").value("bearer"))
                .andExpect(jsonPath("$.paths['/enderecos/cep/{cep}']").exists())
                .andExpect(jsonPath("$.paths['/login'].post.security").isEmpty());
        mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
    }
    @Test void recursoProtegidoExigeToken() throws Exception {
        mvc.perform(get("/alunos")).andExpect(status().isUnauthorized());
        mvc.perform(get("/enderecos/cep/01001000")).andExpect(status().isUnauthorized());
    }
    @Test void preflightPermitidoSemJwt() throws Exception {
        mvc.perform(options("/alunos").header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "Authorization,Content-Type"))
                .andExpect(status().isOk()).andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }
    @Test void origemNaoPermitidaBloqueada() throws Exception {
        mvc.perform(options("/alunos").header("Origin", "https://nao-permitido.example")
                .header("Access-Control-Request-Method", "POST")).andExpect(status().isForbidden());
    }
    @Test void loginValidoRetornaTokenELoginInvalidoRetorna401() throws Exception {
        usuarios.saveAndFlush(new Usuario("admin", new BCryptPasswordEncoder().encode("admin"), Role.ADMIN));
        var resultado = mvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content("{\"login\":\"admin\",\"senha\":\"admin\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.tokenJWT").isNotEmpty()).andReturn();
        String token = resultado.getResponse().getContentAsString().split("\"")[3];
        mvc.perform(get("/alunos").header("Authorization", "Bearer " + token)).andExpect(status().isOk());
        mvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content("{\"login\":\"admin\",\"senha\":\"errada\"}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/alunos").header("Authorization", "Bearer invalido")).andExpect(status().isUnauthorized());
    }
    @Test void hashDoAdministradorInicialCorrespondeACredencialDocumentada() throws Exception {
        String migration;
        try (var stream = getClass().getResourceAsStream("/db/migration/V9__insert-admin.sql")) {
            migration = new String(java.util.Objects.requireNonNull(stream).readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        }
        var matcher = java.util.regex.Pattern.compile("\\$2[aby]\\$[0-9]{2}\\$[./A-Za-z0-9]{53}").matcher(migration);
        assertThat(matcher.find()).isTrue();
        assertThat(new BCryptPasswordEncoder().matches("admin", matcher.group())).isTrue();
    }
}
