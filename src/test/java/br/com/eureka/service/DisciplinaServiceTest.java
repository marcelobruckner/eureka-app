package br.com.eureka.service;

import br.com.eureka.form.DisciplinaForm;
import br.com.eureka.model.Aluno;
import br.com.eureka.model.AnoLetivo;
import br.com.eureka.model.Disciplina;
import br.com.eureka.repository.AlunoRepository;
import br.com.eureka.repository.AnoLetivoRepository;
import br.com.eureka.repository.DisciplinaRepository;
import br.com.eureka.repository.TarefaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class DisciplinaServiceTest {

    @Autowired
    private DisciplinaService disciplinaService;

    @Autowired
    private AnoLetivoService anoLetivoService;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private AnoLetivoRepository anoLetivoRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private TarefaRepository tarefaRepository;

    @BeforeEach
    void limparDados() {
        tarefaRepository.deleteAll();
        disciplinaRepository.deleteAll();
        anoLetivoRepository.deleteAll();
        alunoRepository.deleteAll();
    }

    @Test
    void deveCadastrarDisciplinaNoAnoDoAlunoLogado() {
        Aluno aluno = alunoRepository.save(Aluno.criar("Ana", "ana@escola.com", "ana", "senha"));
        AnoLetivo anoLetivo = anoLetivoRepository.save(AnoLetivo.criar(2026, aluno));

        DisciplinaForm form = new DisciplinaForm();
        form.setNome("Matematica");
        form.setAnoLetivoId(anoLetivo.getId());

        Disciplina disciplina = disciplinaService.cadastrar("ana", form);

        assertEquals("Matematica", disciplina.getNome());
        assertEquals(anoLetivo.getId(), disciplina.getAnoLetivo().getId());
        assertTrue(disciplinaService.listarDoAnoLetivo("ana", anoLetivo.getId()).size() == 1);
    }

    @Test
    void deveBloquearDisciplinaEmAnoLetivoDeOutroAluno() {
        Aluno ana = alunoRepository.save(Aluno.criar("Ana", "ana@escola.com", "ana", "senha"));
        Aluno bia = alunoRepository.save(Aluno.criar("Bia", "bia@escola.com", "bia", "senha"));
        AnoLetivo anoDaBia = anoLetivoRepository.save(AnoLetivo.criar(2026, bia));

        DisciplinaForm form = new DisciplinaForm();
        form.setNome("Historia");
        form.setAnoLetivoId(anoDaBia.getId());

        assertThrows(IllegalArgumentException.class, () -> disciplinaService.cadastrar("ana", form));
    }
}
