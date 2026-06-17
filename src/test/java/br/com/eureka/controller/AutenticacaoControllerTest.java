package br.com.eureka.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import br.com.eureka.model.Aluno;
import br.com.eureka.model.AnoLetivo;
import br.com.eureka.model.Disciplina;
import br.com.eureka.model.Tarefa;
import br.com.eureka.repository.AlunoRepository;
import br.com.eureka.repository.AnoLetivoRepository;
import br.com.eureka.repository.DisciplinaRepository;
import br.com.eureka.repository.TarefaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
class AutenticacaoControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private AnoLetivoRepository anoLetivoRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private TarefaRepository tarefaRepository;

    private static final ZoneId TIME_ZONE = ZoneId.of("America/Sao_Paulo");

    private MockMvc mockMvc;

    @BeforeEach
    void configurarMockMvc() {
        mockMvc = webAppContextSetup(context).apply(springSecurity()).build();
        tarefaRepository.deleteAll();
        disciplinaRepository.deleteAll();
        anoLetivoRepository.deleteAll();
        alunoRepository.deleteAll();
    }

    @Test
    void deveExibirTelaDeLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void deveRedirecionarParaLoginQuandoNaoAutenticado() throws Exception {
        mockMvc.perform(get("/inicio"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void deveCadastrarAlunoERedirecionarParaLogin() throws Exception {
        mockMvc.perform(post("/registro")
                        .with(csrf())
                        .param("nome", "Ana")
                        .param("usuario", "ana")
                        .param("email", "ana@escola.com")
                        .param("senha", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?cadastro"));
    }

    @Test
    @WithMockUser(username = "ana")
    void devePermitirAcessoAAreaProtegida() throws Exception {
        mockMvc.perform(get("/inicio"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ana")));
    }

    @Test
    @WithMockUser(username = "ana")
    void deveExibirDashboardComAnoEDisciplinaSemFalharNaView() throws Exception {
        Aluno aluno = alunoRepository.save(Aluno.criar("Ana", "ana@escola.com", "ana", "senha"));
        AnoLetivo anoLetivo = anoLetivoRepository.save(AnoLetivo.criar(2026, aluno));
        Disciplina disciplina = disciplinaRepository.save(Disciplina.criar("Matematica", anoLetivo));
        tarefaRepository.save(Tarefa.criar("Lista 1", LocalDate.now(TIME_ZONE).plusDays(1), disciplina,
                Clock.fixed(Instant.parse("2026-06-17T12:00:00Z"), TIME_ZONE)));

        mockMvc.perform(get("/inicio"))
                .andExpect(status().isOk())
                .andExpect(view().name("inicio"))
                .andExpect(content().string(containsString("Matematica")))
                .andExpect(content().string(containsString("2026")))
                .andExpect(content().string(containsString("Lista 1")));
    }

    @Test
    @WithMockUser(username = "ana")
    void deveFiltrarDashboardPorSituacaoDaTarefa() throws Exception {
        Aluno aluno = alunoRepository.save(Aluno.criar("Ana", "ana@escola.com", "ana", "senha"));
        AnoLetivo anoLetivo = anoLetivoRepository.save(AnoLetivo.criar(2026, aluno));
        Disciplina disciplina = disciplinaRepository.save(Disciplina.criar("Matematica", anoLetivo));

        tarefaRepository.save(Tarefa.criar(
                "Lista vencida",
                LocalDate.now(TIME_ZONE).minusDays(1),
                disciplina,
                Clock.fixed(Instant.parse("2026-06-15T12:00:00Z"), TIME_ZONE)
        ));
        Tarefa entregue = tarefaRepository.save(Tarefa.criar(
                "Lista entregue",
                LocalDate.now(TIME_ZONE),
                disciplina,
                Clock.fixed(Instant.parse("2026-06-16T12:00:00Z"), TIME_ZONE)
        ));
        entregue.entregarEm(LocalDate.now(TIME_ZONE));

        mockMvc.perform(get("/inicio")
                        .param("anoLetivoId", anoLetivo.getId().toString())
                        .param("situacaoTarefa", "VENCIDA"))
                .andExpect(status().isOk())
                .andExpect(view().name("inicio"))
                .andExpect(content().string(containsString("Lista vencida")))
                .andExpect(content().string(not(containsString("Lista entregue"))));
    }

    @Test
    void deveEfetuarLogout() throws Exception {
        mockMvc.perform(post("/logout").with(csrf()))
                .andExpect(status().is3xxRedirection());
    }
}
