package br.com.eureka.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import static org.hamcrest.Matchers.containsString;
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

    private MockMvc mockMvc;

    @BeforeEach
    void configurarMockMvc() {
        mockMvc = webAppContextSetup(context).apply(springSecurity()).build();
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
    void deveEfetuarLogout() throws Exception {
        mockMvc.perform(post("/logout").with(csrf()))
                .andExpect(status().is3xxRedirection());
    }
}
