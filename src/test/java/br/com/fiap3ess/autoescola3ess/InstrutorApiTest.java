package br.com.fiap3ess.autoescola3ess;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.*;

@WithMockUser(roles = "ADMIN")
class InstrutorApiTest extends ApiTestBase {
    @Test void cadastrarEConsultarInstrutor() throws Exception {
        mvc.perform(post("/instrutores").contentType(MediaType.APPLICATION_JSON).content(
                "{\"nome\":\"Carlos\",\"email\":\"carlos@example.com\",\"telefone\":\"11999999999\",\"cnh\":\"12345678901\",\"especialidade\":\"CARROS\"," + jsonEndereco() + "}"))
                .andExpect(status().isCreated()).andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.especialidade").value("CARROS"));
        var salvo = instrutores.findAll().get(0);
        mvc.perform(get("/instrutores/{id}", salvo.getId())).andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Carlos"));
        mvc.perform(get("/instrutores")).andExpect(status().isOk()).andExpect(jsonPath("$.content[0].nome").value("Carlos"));
    }
    @Test void atualizarEExcluirLogicamente() throws Exception {
        var i = instrutor();
        mvc.perform(put("/instrutores").contentType(MediaType.APPLICATION_JSON).content("{\"id\":" + i.getId() + ",\"nome\":\"Carlos Silva\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Carlos Silva"));
        mvc.perform(delete("/instrutores/{id}", i.getId())).andExpect(status().isNoContent());
        assertThat(instrutores.findById(i.getId()).orElseThrow().isAtivo()).isFalse();
        mvc.perform(get("/instrutores")).andExpect(jsonPath("$.content").isEmpty());
    }
    @Test void rejeitarCnhDuplicada() throws Exception {
        instrutor();
        mvc.perform(post("/instrutores").contentType(MediaType.APPLICATION_JSON).content(
                "{\"nome\":\"Outro\",\"email\":\"outro@example.com\",\"telefone\":\"11999999999\",\"cnh\":\"12345678901\",\"especialidade\":\"CARROS\"," + jsonEndereco() + "}"))
                .andExpect(status().isBadRequest());
    }
    @Test void rejeitarCadastroInvalido() throws Exception {
        mvc.perform(post("/instrutores").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }
    @Test void instrutorInexistenteRetorna404() throws Exception {
        mvc.perform(get("/instrutores/999999")).andExpect(status().isNotFound());
    }
}
