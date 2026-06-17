package br.com.eureka.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import br.com.eureka.model.Aluno;
import br.com.eureka.model.AnoLetivo;
import br.com.eureka.repository.AlunoRepository;
import br.com.eureka.repository.AnoLetivoRepository;
import br.com.eureka.repository.DisciplinaRepository;
import br.com.eureka.repository.TarefaRepository;

@SpringBootTest
class AnoLetivoControllerTest {

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
    void deveExibirPaginaDeAnosLetivos() throws Exception {
        Aluno aluno = alunoRepository.save(Aluno.criar("Ana", "ana@escola.com", "ana", "senha"));
        anoLetivoRepository.save(AnoLetivo.criar(2026, aluno));

        mockMvc.perform(get("/anos-letivos"))
                .andExpect(status().isOk())
                .andExpect(view().name("anos-letivos"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().string(containsString("Anos letivos")))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().string(containsString("2026")))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().string(containsString("Cadastro rápido")));
    }

    @Test
    @WithMockUser(username = "ana")
    void deveCadastrarAnoLetivoERedirecionar() throws Exception {
        alunoRepository.save(Aluno.criar("Ana", "ana@escola.com", "ana", "senha"));

        mockMvc.perform(post("/anos-letivos")
                        .with(csrf())
                        .param("ano", "2026"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/anos-letivos"));
    }

    @Test
    @WithMockUser(username = "ana")
    void deveExibirResumoDeErroAoTentarSalvarAnoInvalido() throws Exception {
        alunoRepository.save(Aluno.criar("Ana", "ana@escola.com", "ana", "senha"));

        mockMvc.perform(post("/anos-letivos")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("anos-letivos"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .string(containsString("Corrija os campos destacados para salvar o ano letivo.")))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .string(containsString("is-invalid")));
    }
}
