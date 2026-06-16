package br.com.eureka.service;

import br.com.eureka.form.CadastroAlunoForm;
import br.com.eureka.model.Aluno;
import br.com.eureka.repository.AlunoRepository;
import br.com.eureka.repository.AnoLetivoRepository;
import br.com.eureka.repository.DisciplinaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class AlunoServiceTest {

    @Autowired
    private AlunoService alunoService;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private AnoLetivoRepository anoLetivoRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void limparDados() {
        disciplinaRepository.deleteAll();
        anoLetivoRepository.deleteAll();
        alunoRepository.deleteAll();
    }

    @Test
    void deveCadastrarAlunoComSenhaCriptografada() {
        CadastroAlunoForm form = new CadastroAlunoForm();
        form.setNome("Ana Clara");
        form.setUsuario("ana-clara");
        form.setEmail("ana.clara@escola.com");
        form.setSenha("123456");

        Aluno aluno = alunoService.cadastrar(form);

        assertEquals("Ana Clara", aluno.getNome());
        assertEquals("ana-clara", aluno.getUsuario());
        assertEquals("ana.clara@escola.com", aluno.getEmail());
        assertTrue(passwordEncoder.matches("123456", aluno.getSenha()));
        assertTrue(alunoRepository.findByUsuarioAndExcluidoFalse("ana-clara").isPresent());
    }

    @Test
    void deveBloquearUsuarioDuplicadoAtivo() {
        CadastroAlunoForm form = new CadastroAlunoForm();
        form.setNome("Ana Clara");
        form.setUsuario("ana-clara");
        form.setEmail("ana.clara@escola.com");
        form.setSenha("123456");

        alunoService.cadastrar(form);

        assertThrows(CadastroAlunoException.class, () -> alunoService.cadastrar(form));
    }
}
