package br.com.fiap3ess.autoescola3ess;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.*;

import br.com.fiap3ess.autoescola3ess.domain.usuario.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@WithMockUser(roles = "ADMIN")
class UsuarioApiTest extends ApiTestBase {
    @Test void cadastrarComSenhaBCryptEConsultarSemExporSenha() throws Exception {
        mvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"jessica\",\"senha\":\"senha123\",\"perfil\":\"USER\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.senha").doesNotExist());
        var u = usuarios.findByLogin("jessica");
        assertThat(u.getPassword()).isNotEqualTo("senha123");
        assertThat(new BCryptPasswordEncoder().matches("senha123", u.getPassword())).isTrue();
        mvc.perform(get("/usuarios/{id}", u.getId())).andExpect(status().isOk()).andExpect(jsonPath("$.login").value("jessica"));
        mvc.perform(get("/usuarios")).andExpect(status().isOk()).andExpect(jsonPath("$.content[0].senha").doesNotExist());
    }
    @Test void alterarPerfilEExcluirUsuario() throws Exception {
        var u = usuarios.save(new Usuario("jessica", "hash", Role.USER));
        mvc.perform(put("/usuarios/{id}/perfil", u.getId()).contentType(MediaType.APPLICATION_JSON).content("{\"perfil\":\"ADMIN\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.perfil").value("ADMIN"));
        mvc.perform(delete("/usuarios/{id}", u.getId())).andExpect(status().isNoContent());
        assertThat(usuarios.existsById(u.getId())).isFalse();
    }
    @Test void rejeitarLoginDuplicado() throws Exception {
        usuarios.save(new Usuario("jessica", "hash", Role.USER));
        mvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"jessica\",\"senha\":\"senha123\",\"perfil\":\"USER\"}"))
                .andExpect(status().isBadRequest());
    }
    @Test @WithMockUser(username = "jessica", roles = "USER")
    void alterarPropriaSenha() throws Exception {
        usuarios.save(new Usuario("jessica", new BCryptPasswordEncoder().encode("senha123"), Role.USER));
        mvc.perform(patch("/usuarios/minha-senha").contentType(MediaType.APPLICATION_JSON)
                .content("{\"senhaAtual\":\"senha123\",\"novaSenha\":\"novaSenha456\"}"))
                .andExpect(status().isNoContent());
        assertThat(new BCryptPasswordEncoder().matches("novaSenha456", usuarios.findByLogin("jessica").getPassword())).isTrue();
    }
    @Test @WithMockUser(roles = "USER") void usuarioComumNaoPodeAdministrarUsuarios() throws Exception {
        mvc.perform(get("/usuarios")).andExpect(status().isForbidden());
    }
}
