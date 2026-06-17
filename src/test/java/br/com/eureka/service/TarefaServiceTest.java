package br.com.eureka.service;

import br.com.eureka.form.ConcluirTarefaForm;
import br.com.eureka.form.TarefaForm;
import br.com.eureka.model.Aluno;
import br.com.eureka.model.AnoLetivo;
import br.com.eureka.model.Disciplina;
import br.com.eureka.model.StatusTarefa;
import br.com.eureka.model.SituacaoPrazoTarefa;
import br.com.eureka.model.Tarefa;
import br.com.eureka.repository.AlunoRepository;
import br.com.eureka.repository.AnoLetivoRepository;
import br.com.eureka.repository.DisciplinaRepository;
import br.com.eureka.repository.TarefaRepository;
import br.com.eureka.view.DisciplinaTarefasView;
import br.com.eureka.view.TarefaResumoView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class TarefaServiceTest {

    private static final ZoneId TIME_ZONE = ZoneId.of("America/Sao_Paulo");

    @Autowired
    private TarefaService tarefaService;

    @Autowired
    private DisciplinaService disciplinaService;

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
    void deveCadastrarTarefaNaDisciplinaDoAlunoLogado() {
        Aluno aluno = alunoRepository.save(Aluno.criar("Ana", "ana@escola.com", "ana", "senha"));
        AnoLetivo anoLetivo = anoLetivoRepository.save(AnoLetivo.criar(2026, aluno));
        Disciplina disciplina = disciplinaRepository.save(Disciplina.criar("Matematica", anoLetivo));

        TarefaForm form = new TarefaForm();
        form.setNome("Lista 1");
        form.setDataPrevistaEntrega(LocalDate.now(TIME_ZONE));
        form.setDisciplinaId(disciplina.getId());

        Tarefa tarefa = tarefaService.cadastrar("ana", form);

        assertEquals("Lista 1", tarefa.getNome());
        assertEquals(StatusTarefa.PENDENTE, tarefa.getStatus());
        assertEquals(disciplina.getId(), tarefa.getDisciplina().getId());
    }

    @Test
    void deveListarTarefasComPrazoVencidoDeFormaCalculada() {
        Aluno aluno = alunoRepository.save(Aluno.criar("Ana", "ana@escola.com", "ana", "senha"));
        AnoLetivo anoLetivo = anoLetivoRepository.save(AnoLetivo.criar(2026, aluno));
        Disciplina disciplina = disciplinaRepository.save(Disciplina.criar("Matematica", anoLetivo));

        Clock clockPassado = Clock.fixed(Instant.parse("2026-06-15T12:00:00Z"), TIME_ZONE);
        Tarefa tarefa = Tarefa.criar("Lista 1", LocalDate.of(2026, 6, 16), disciplina, clockPassado);
        tarefaRepository.save(tarefa);

        List<TarefaResumoView> tarefas = tarefaService.listarDaDisciplina("ana", disciplina.getId());

        assertEquals(1, tarefas.size());
        assertEquals(SituacaoPrazoTarefa.VENCIDA, tarefas.get(0).situacaoPrazo());
        assertTrue(tarefas.get(0).isVencida());
        assertEquals(StatusTarefa.PENDENTE, tarefas.get(0).status());
    }

    @Test
    void deveAgruparTarefasPorDisciplinaNoAnoLetivo() {
        Aluno aluno = alunoRepository.save(Aluno.criar("Ana", "ana@escola.com", "ana", "senha"));
        AnoLetivo anoLetivo = anoLetivoRepository.save(AnoLetivo.criar(2026, aluno));
        Disciplina matematica = disciplinaRepository.save(Disciplina.criar("Matematica", anoLetivo));
        Disciplina portugues = disciplinaRepository.save(Disciplina.criar("Portugues", anoLetivo));

        tarefaRepository.save(Tarefa.criar("Lista 1", LocalDate.now(TIME_ZONE).plusDays(1), matematica,
                Clock.fixed(Instant.parse("2026-06-17T12:00:00Z"), TIME_ZONE)));
        tarefaRepository.save(Tarefa.criar("Redacao", LocalDate.now(TIME_ZONE).plusDays(2), portugues,
                Clock.fixed(Instant.parse("2026-06-17T12:00:00Z"), TIME_ZONE)));

        List<DisciplinaTarefasView> blocos = tarefaService.listarAgrupadasPorAnoLetivo("ana", anoLetivo.getId());

        assertEquals(2, blocos.size());
        assertEquals("Matematica", blocos.get(0).disciplinaNome());
        assertEquals(1, blocos.get(0).tarefas().size());
        assertEquals("Portugues", blocos.get(1).disciplinaNome());
    }

    @Test
    void deveConcluirTarefaSemPermitirReabertura() {
        Aluno aluno = alunoRepository.save(Aluno.criar("Ana", "ana@escola.com", "ana", "senha"));
        AnoLetivo anoLetivo = anoLetivoRepository.save(AnoLetivo.criar(2026, aluno));
        Disciplina disciplina = disciplinaRepository.save(Disciplina.criar("Matematica", anoLetivo));
        Tarefa tarefa = tarefaRepository.save(Tarefa.criar("Lista 1", LocalDate.now(TIME_ZONE).plusDays(1), disciplina,
                Clock.fixed(Instant.parse("2026-06-17T12:00:00Z"), TIME_ZONE)));

        ConcluirTarefaForm form = new ConcluirTarefaForm();
        form.setDataEntrega(LocalDate.now(TIME_ZONE));

        tarefaService.concluir("ana", tarefa.getId(), form);

        Tarefa tarefaAtualizada = tarefaService.obterDoUsuario("ana", tarefa.getId());
        assertEquals(StatusTarefa.ENTREGUE, tarefaAtualizada.getStatus());
        assertThrows(IllegalStateException.class, () -> tarefaService.concluir("ana", tarefa.getId(), form));
    }

    @Test
    void deveBloquearConcluirTarefaDeOutroAluno() {
        Aluno ana = alunoRepository.save(Aluno.criar("Ana", "ana@escola.com", "ana", "senha"));
        Aluno bia = alunoRepository.save(Aluno.criar("Bia", "bia@escola.com", "bia", "senha"));
        AnoLetivo anoDaBia = anoLetivoRepository.save(AnoLetivo.criar(2026, bia));
        Disciplina disciplina = disciplinaRepository.save(Disciplina.criar("Matematica", anoDaBia));
        Tarefa tarefa = tarefaRepository.save(Tarefa.criar("Lista 1", LocalDate.now(TIME_ZONE).plusDays(1), disciplina,
                Clock.fixed(Instant.parse("2026-06-17T12:00:00Z"), TIME_ZONE)));

        ConcluirTarefaForm form = new ConcluirTarefaForm();
        form.setDataEntrega(LocalDate.now(TIME_ZONE));

        assertThrows(IllegalArgumentException.class, () -> tarefaService.concluir("ana", tarefa.getId(), form));
    }
}
