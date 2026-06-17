package br.com.eureka.controller;

import br.com.eureka.model.Aluno;
import br.com.eureka.model.AnoLetivo;
import br.com.eureka.model.Disciplina;
import br.com.eureka.repository.AlunoRepository;
import br.com.eureka.repository.AnoLetivoRepository;
import br.com.eureka.repository.DisciplinaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DisciplinaControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private AnoLetivoRepository anoLetivoRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void configurarMockMvc() {
        mockMvc = webAppContextSetup(context).apply(springSecurity()).build();
        disciplinaRepository.deleteAll();
        anoLetivoRepository.deleteAll();
        alunoRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "ana")
    void deveExibirPaginaDeDisciplinasDoAnoSelecionado() throws Exception {
        Aluno aluno = alunoRepository.save(Aluno.criar("Ana", "ana@escola.com", "ana", "senha"));
        AnoLetivo ano = anoLetivoRepository.save(AnoLetivo.criar(2026, aluno));
        disciplinaRepository.save(Disciplina.criar("Matematica", ano));

        mockMvc.perform(get("/anos-letivos/" + ano.getId() + "/disciplinas"))
                .andExpect(status().isOk())
                .andExpect(view().name("disciplinas"))
                .andExpect(model().attribute("disciplinaForm", hasProperty("anoLetivoId", is(ano.getId()))))
                .andExpect(content().string(containsString("Matematica")));
    }

    @Test
    @WithMockUser(username = "ana")
    void deveManterDisciplinasAoVoltarComErroDeValidacao() throws Exception {
        Aluno aluno = alunoRepository.save(Aluno.criar("Ana", "ana@escola.com", "ana", "senha"));
        AnoLetivo ano = anoLetivoRepository.save(AnoLetivo.criar(2026, aluno));
        disciplinaRepository.save(Disciplina.criar("Matematica", ano));

        mockMvc.perform(post("/disciplinas")
                        .with(csrf())
                        .param("anoLetivoId", ano.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("disciplinas"))
                .andExpect(model().attribute("disciplinaForm", hasProperty("anoLetivoId", is(ano.getId()))))
                .andExpect(content().string(containsString("Matematica")))
                .andExpect(content().string(containsString("Nome e obrigatorio")));
    }

    @Test
    @WithMockUser(username = "ana")
    void deveManterDisciplinasQuandoAnoForLimpoNoFormulario() throws Exception {
        Aluno aluno = alunoRepository.save(Aluno.criar("Ana", "ana@escola.com", "ana", "senha"));
        AnoLetivo ano = anoLetivoRepository.save(AnoLetivo.criar(2026, aluno));
        disciplinaRepository.save(Disciplina.criar("Matematica", ano));

        mockMvc.perform(post("/disciplinas")
                        .with(csrf())
                        .param("nome", "Fisica")
                        .param("anoSelecionadoId", ano.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("disciplinas"))
                .andExpect(model().attribute("disciplinaForm", hasProperty("anoSelecionadoId", is(ano.getId()))))
                .andExpect(content().string(containsString("Matematica")))
                .andExpect(content().string(containsString("Ano letivo e obrigatorio")));
    }

    @Test
    @WithMockUser(username = "ana")
    void deveCadastrarDisciplinaERedirecionarParaAnoSelecionado() throws Exception {
        Aluno aluno = alunoRepository.save(Aluno.criar("Ana", "ana@escola.com", "ana", "senha"));
        AnoLetivo ano = anoLetivoRepository.save(AnoLetivo.criar(2026, aluno));

        mockMvc.perform(post("/disciplinas")
                        .with(csrf())
                        .param("nome", "Matematica")
                        .param("anoLetivoId", ano.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/anos-letivos/" + ano.getId() + "/disciplinas"));

        assertEquals(1, disciplinaRepository.findByAnoLetivoIdAndExcluidoFalseOrderByNomeAsc(ano.getId()).size());
    }
}
