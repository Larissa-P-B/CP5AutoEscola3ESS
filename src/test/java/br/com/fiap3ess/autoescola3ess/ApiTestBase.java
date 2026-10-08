package br.com.fiap3ess.autoescola3ess;

import br.com.fiap3ess.autoescola3ess.domain.aluno.*;
import br.com.fiap3ess.autoescola3ess.domain.instrutor.*;
import br.com.fiap3ess.autoescola3ess.domain.usuario.*;
import br.com.fiap3ess.autoescola3ess.domain.agenda.*;
import br.com.fiap3ess.autoescola3ess.domain.endereco.DadosEndereco;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.DayOfWeek;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
abstract class ApiTestBase {
    @Autowired protected MockMvc mvc;
    @Autowired protected AlunoRepository alunos;
    @Autowired protected InstrutorRepository instrutores;
    @Autowired protected UsuarioRepository usuarios;
    @Autowired protected InstrucaoRepository instrucoes;

    protected DadosEndereco endereco() {
        return new DadosEndereco("Rua A", "10", null, "Centro", "São Paulo", "SP", "01001-000");
    }
    protected Aluno aluno() {
        return alunos.save(new Aluno(new DadosCadastroAluno("Ana", "ana@example.com", "11999999999", "12345678901", endereco())));
    }
    protected Instrutor instrutor() {
        return instrutores.save(new Instrutor(new DadosCadastroInstrutor("Carlos", "carlos@example.com", "11988888888",
                "12345678901", Especialidade.CARROS, endereco())));
    }
    protected LocalDateTime horario() {
        LocalDateTime data = LocalDateTime.now().plusDays(7).withHour(10).withMinute(0).withSecond(0).withNano(0);
        return data.getDayOfWeek() == DayOfWeek.SUNDAY ? data.plusDays(1) : data;
    }
    protected String agendamento(Long aluno, Long instrutor, LocalDateTime data) {
        return "{\"id_aluno\":" + aluno + ",\"id_instrutor\":" + instrutor + ",\"data_hora\":\""
                + data.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm")) + "\"}";
    }
    protected String jsonEndereco() {
        return "\"endereco\":{\"logradouro\":\"Rua A\",\"numero\":\"10\",\"bairro\":\"Centro\","
                + "\"cidade\":\"São Paulo\",\"uf\":\"SP\",\"cep\":\"01001-000\"}";
    }
}
