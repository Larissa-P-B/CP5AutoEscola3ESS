package br.com.fiap3ess.autoescola3ess;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.*;

@WithMockUser(roles = "ADMIN")
class AlunoApiTest extends ApiTestBase {
    @Test void cadastrarEConsultarAluno() throws Exception {
        mvc.perform(post("/alunos").contentType(MediaType.APPLICATION_JSON).content(
                "{\"nome\":\"Ana\",\"email\":\"ana@example.com\",\"telefone\":\"11999999999\",\"cpf\":\"12345678901\"," + jsonEndereco() + "}"))
                .andExpect(status().isCreated()).andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.ativo").value(true));
        var salvo = alunos.findAll().get(0);
        mvc.perform(get("/alunos/{id}", salvo.getId())).andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Ana"));
        mvc.perform(get("/alunos")).andExpect(status().isOk()).andExpect(jsonPath("$.content[0].nome").value("Ana"));
    }
    @Test void atualizarEExcluirLogicamente() throws Exception {
        var a = aluno();
        mvc.perform(put("/alunos").contentType(MediaType.APPLICATION_JSON).content("{\"id\":" + a.getId() + ",\"nome\":\"Ana Silva\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Ana Silva"));
        mvc.perform(delete("/alunos/{id}", a.getId())).andExpect(status().isNoContent());
        assertThat(alunos.findById(a.getId()).orElseThrow().isAtivo()).isFalse();
        mvc.perform(get("/alunos")).andExpect(jsonPath("$.content").isEmpty());
    }
    @Test void rejeitarCadastroInvalido() throws Exception {
        mvc.perform(post("/alunos").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }
    @Test void rejeitarCpfDuplicado() throws Exception {
        aluno();
        mvc.perform(post("/alunos").contentType(MediaType.APPLICATION_JSON).content(
                "{\"nome\":\"Bia\",\"email\":\"bia@example.com\",\"telefone\":\"11999999999\",\"cpf\":\"12345678901\"," + jsonEndereco() + "}"))
                .andExpect(status().isBadRequest());
    }
    @Test void alunoInexistenteRetorna404() throws Exception {
        mvc.perform(get("/alunos/999999")).andExpect(status().isNotFound());
    }
}
