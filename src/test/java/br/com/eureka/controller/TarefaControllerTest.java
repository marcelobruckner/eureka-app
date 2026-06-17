package br.com.eureka.controller;

import br.com.eureka.model.Aluno;
import br.com.eureka.model.AnoLetivo;
import br.com.eureka.model.Disciplina;
import br.com.eureka.model.StatusTarefa;
import br.com.eureka.model.Tarefa;
import br.com.eureka.repository.AlunoRepository;
import br.com.eureka.repository.AnoLetivoRepository;
import br.com.eureka.repository.DisciplinaRepository;
import br.com.eureka.repository.TarefaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
class TarefaControllerTest {

    private static final ZoneId TIME_ZONE = ZoneId.of("America/Sao_Paulo");

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
    @WithMockUser(username = "ana")
    void deveExibirListaDeTarefasDaDisciplina() throws Exception {
        Disciplina disciplina = criarDisciplinaDaAna();
        criarTarefa(disciplina, "Lista 1", LocalDate.now(TIME_ZONE).plusDays(1));

        mockMvc.perform(get("/disciplinas/" + disciplina.getId() + "/tarefas"))
                .andExpect(status().isOk())
                .andExpect(view().name("tarefas"))
                .andExpect(content().string(containsString("tarefas na disciplina")))
                .andExpect(content().string(containsString("Lista 1")))
                .andExpect(content().string(containsString("Matematica")));
    }

    @Test
    @WithMockUser(username = "ana")
    void deveCadastrarTarefaERedirecionarParaListaDaDisciplina() throws Exception {
        Disciplina disciplina = criarDisciplinaDaAna();

        mockMvc.perform(post("/tarefas")
                        .with(csrf())
                        .param("nome", "Lista 1")
                        .param("dataPrevistaEntrega", LocalDate.now(TIME_ZONE).plusDays(1).toString())
                        .param("disciplinaId", disciplina.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/disciplinas/" + disciplina.getId() + "/tarefas"));
    }

    @Test
    @WithMockUser(username = "ana")
    void deveExibirErroQuandoFaltarNomeDaTarefa() throws Exception {
        Disciplina disciplina = criarDisciplinaDaAna();

        mockMvc.perform(post("/tarefas")
                        .with(csrf())
                        .param("dataPrevistaEntrega", LocalDate.now(TIME_ZONE).plusDays(1).toString())
                        .param("disciplinaId", disciplina.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("tarefas"))
                .andExpect(content().string(containsString("Nome e obrigatorio")));
    }

    @Test
    @WithMockUser(username = "ana")
    void deveConcluirTarefaERedirecionarParaLista() throws Exception {
        Disciplina disciplina = criarDisciplinaDaAna();
        Tarefa tarefa = criarTarefa(disciplina, "Lista 1", LocalDate.now(TIME_ZONE).plusDays(1));

        mockMvc.perform(post("/tarefas/" + tarefa.getId() + "/concluir")
                        .with(csrf())
                        .param("dataEntrega", LocalDate.now(TIME_ZONE).toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/disciplinas/" + disciplina.getId() + "/tarefas"));

        assertEquals(StatusTarefa.ENTREGUE, tarefaRepository.findById(tarefa.getId()).orElseThrow().getStatus());
    }

    @Test
    @WithMockUser(username = "ana")
    void deveBloquearReaberturaDeTarefaEntregue() throws Exception {
        Disciplina disciplina = criarDisciplinaDaAna();
        Tarefa tarefa = criarTarefa(disciplina, "Lista 1", LocalDate.now(TIME_ZONE).plusDays(1));
        tarefa.entregarEm(LocalDate.now(TIME_ZONE));
        tarefaRepository.save(tarefa);

        mockMvc.perform(get("/tarefas/" + tarefa.getId() + "/concluir"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/disciplinas/" + disciplina.getId() + "/tarefas"));
    }

    private Disciplina criarDisciplinaDaAna() {
        Aluno aluno = alunoRepository.save(Aluno.criar("Ana", "ana@escola.com", "ana", "senha"));
        AnoLetivo anoLetivo = anoLetivoRepository.save(AnoLetivo.criar(2026, aluno));
        return disciplinaRepository.save(Disciplina.criar("Matematica", anoLetivo));
    }

    private Tarefa criarTarefa(Disciplina disciplina, String nome, LocalDate dataPrevistaEntrega) {
        return tarefaRepository.save(Tarefa.criar(
                nome,
                dataPrevistaEntrega,
                disciplina,
                Clock.fixed(Instant.parse("2026-06-17T12:00:00Z"), TIME_ZONE)
        ));
    }
}
