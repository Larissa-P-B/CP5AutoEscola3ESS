package br.com.fiap3ess.autoescola3ess;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.*;

import br.com.fiap3ess.autoescola3ess.domain.agenda.*;

@WithMockUser(roles = "USER")
class InstrucaoApiTest extends ApiTestBase {
    @Test void agendarListarDetalharReagendarECancelar() throws Exception {
        var a = aluno(); var i = instrutor(); var h = horario();
        mvc.perform(post("/instrucoes").contentType(MediaType.APPLICATION_JSON).content(agendamento(a.getId(), i.getId(), h)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("AGENDADA"));
        var id = instrucoes.findAll().get(0).getId();
        mvc.perform(get("/instrucoes")).andExpect(status().isOk()).andExpect(jsonPath("$.content[0].nome_aluno").value("Ana"));
        mvc.perform(get("/instrucoes/{id}", id)).andExpect(status().isOk());
        // Manter o mesmo horário não deve gerar conflito com a própria instrução.
        mvc.perform(put("/instrucoes/{id}", id).contentType(MediaType.APPLICATION_JSON).content(agendamento(a.getId(), i.getId(), h)))
                .andExpect(status().isOk());
        mvc.perform(put("/instrucoes/{id}", id).contentType(MediaType.APPLICATION_JSON).content(agendamento(a.getId(), i.getId(), h.plusHours(1))))
                .andExpect(status().isOk());
        mvc.perform(patch("/instrucoes/{id}/cancelamento", id).contentType(MediaType.APPLICATION_JSON).content("{\"motivo\":\"ALUNO_DESISTIU\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("CANCELADA"))
                .andExpect(jsonPath("$.motivo_cancelamento").value("ALUNO_DESISTIU"));
        assertThat(instrucoes.findById(id).orElseThrow().getDataCancelamento()).isNotNull();
        mvc.perform(patch("/instrucoes/{id}/cancelamento", id).contentType(MediaType.APPLICATION_JSON).content("{\"motivo\":\"OUTROS\"}"))
                .andExpect(status().isBadRequest());
    }
    @Test void selecionarInstrutorAutomaticamente() throws Exception {
        var a = aluno(); instrutor();
        var json = agendamento(a.getId(), null, horario()).replace("\"id_instrutor\":null", "\"especialidade\":\"CARROS\"");
        mvc.perform(post("/instrucoes").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nome_instrutor").value("Carlos"));
    }
    @Test void rejeitarConflitoDeHorario() throws Exception {
        var a = aluno(); var i = instrutor(); var h = horario();
        instrucoes.saveAndFlush(new Instrucao(a, i, h));
        mvc.perform(post("/instrucoes").contentType(MediaType.APPLICATION_JSON).content(agendamento(a.getId(), i.getId(), h)))
                .andExpect(status().isBadRequest());
    }
    @Test void rejeitarTerceiraInstrucaoDoDia() throws Exception {
        var a = aluno(); var i = instrutor(); var h = horario();
        instrucoes.saveAndFlush(new Instrucao(a, i, h));
        instrucoes.saveAndFlush(new Instrucao(a, i, h.plusHours(1)));
        mvc.perform(post("/instrucoes").contentType(MediaType.APPLICATION_JSON).content(agendamento(a.getId(), i.getId(), h.plusHours(2))))
                .andExpect(status().isBadRequest());
    }
    @Test void rejeitarAlunoInativo() throws Exception {
        var a = aluno(); a.excluir(); alunos.flush(); var i = instrutor();
        mvc.perform(post("/instrucoes").contentType(MediaType.APPLICATION_JSON).content(agendamento(a.getId(), i.getId(), horario())))
                .andExpect(status().isBadRequest());
    }
    @Test void rejeitarInstrutorInativo() throws Exception {
        var a = aluno(); var i = instrutor(); i.excluir(); instrutores.flush();
        mvc.perform(post("/instrucoes").contentType(MediaType.APPLICATION_JSON).content(agendamento(a.getId(), i.getId(), horario())))
                .andExpect(status().isBadRequest());
    }
    @Test void rejeitarCancelamentoSemAntecedencia() throws Exception {
        var instrucao = instrucoes.saveAndFlush(new Instrucao(aluno(), instrutor(), java.time.LocalDateTime.now().plusHours(2)));
        mvc.perform(patch("/instrucoes/{id}/cancelamento", instrucao.getId()).contentType(MediaType.APPLICATION_JSON).content("{\"motivo\":\"OUTROS\"}"))
                .andExpect(status().isBadRequest());
    }
    @Test void canceladaNaoOcupaHorarioENaoPodeSerAtualizada() throws Exception {
        var a = aluno(); var i = instrutor(); var h = horario();
        var cancelada = new Instrucao(a, i, h); cancelada.cancelar(MotivoCancelamento.OUTROS); instrucoes.saveAndFlush(cancelada);
        mvc.perform(post("/instrucoes").contentType(MediaType.APPLICATION_JSON).content(agendamento(a.getId(), i.getId(), h)))
                .andExpect(status().isOk());
        mvc.perform(put("/instrucoes/{id}", cancelada.getId()).contentType(MediaType.APPLICATION_JSON).content(agendamento(a.getId(), i.getId(), h)))
                .andExpect(status().isBadRequest());
    }
    @Test void consultaInexistenteRetorna404() throws Exception {
        mvc.perform(get("/instrucoes/999999")).andExpect(status().isNotFound());
    }
    @Test void atualizarNaoContaPropriaInstrucaoNoLimiteDiario() throws Exception {
        var a = aluno(); var i = instrutor(); var h = horario();
        var atual = instrucoes.saveAndFlush(new Instrucao(a, i, h));
        instrucoes.saveAndFlush(new Instrucao(a, i, h.plusHours(1)));
        mvc.perform(put("/instrucoes/{id}", atual.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(agendamento(a.getId(), i.getId(), h.plusHours(2)))).andExpect(status().isOk());
    }
    @Test void atualizacaoRejeitaHorarioOcupadoPorOutraInstrucao() throws Exception {
        var a = aluno(); var i = instrutor(); var h = horario();
        var atual = instrucoes.saveAndFlush(new Instrucao(a, i, h));
        instrucoes.saveAndFlush(new Instrucao(a, i, h.plusHours(1)));
        mvc.perform(put("/instrucoes/{id}", atual.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(agendamento(a.getId(), i.getId(), h.plusHours(1)))).andExpect(status().isBadRequest());
        assertThat(instrucoes.findById(atual.getId()).orElseThrow().getDataHora()).isEqualTo(h);
    }
}
