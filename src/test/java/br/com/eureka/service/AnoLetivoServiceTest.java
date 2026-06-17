package br.com.eureka.service;

import br.com.eureka.form.AnoLetivoForm;
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
class AnoLetivoServiceTest {

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
    void deveCadastrarAnoLetivoParaAlunoLogado() {
        Aluno aluno = alunoRepository.save(Aluno.criar("Ana", "ana@escola.com", "ana", "senha"));

        AnoLetivoForm form = new AnoLetivoForm();
        form.setAno(2026);

        AnoLetivo anoLetivo = anoLetivoService.cadastrar("ana", form);

        assertEquals(2026, anoLetivo.getAno());
        assertEquals(aluno.getId(), anoLetivo.getAluno().getId());
        assertTrue(anoLetivoService.listarDoAluno("ana").size() == 1);
    }

    @Test
    void deveBloquearDuplicidadeDeAnoAtivoDoMesmoAluno() {
        alunoRepository.save(Aluno.criar("Ana", "ana@escola.com", "ana", "senha"));

        AnoLetivoForm form = new AnoLetivoForm();
        form.setAno(2026);

        anoLetivoService.cadastrar("ana", form);

        assertThrows(IllegalArgumentException.class, () -> anoLetivoService.cadastrar("ana", form));
    }

    @Test
    void deveExcluirAnoLetivoSemDisciplinasVinculadas() {
        Aluno aluno = alunoRepository.save(Aluno.criar("Ana", "ana@escola.com", "ana", "senha"));
        AnoLetivo ano = anoLetivoRepository.save(AnoLetivo.criar(2026, aluno));

        anoLetivoService.excluir("ana", ano.getId());

        assertEquals(0, anoLetivoService.listarDoAluno("ana").size());
        assertTrue(anoLetivoRepository.findByIdAndAlunoUsuarioAndExcluidoFalse(ano.getId(), "ana").isEmpty());
    }

    @Test
    void deveBloquearExclusaoDeAnoLetivoComDisciplinasAtivas() {
        Aluno aluno = alunoRepository.save(Aluno.criar("Ana", "ana@escola.com", "ana", "senha"));
        AnoLetivo ano = anoLetivoRepository.save(AnoLetivo.criar(2026, aluno));
        disciplinaRepository.save(Disciplina.criar("Matematica", ano));

        assertThrows(IllegalArgumentException.class, () -> anoLetivoService.excluir("ana", ano.getId()));
    }
}
