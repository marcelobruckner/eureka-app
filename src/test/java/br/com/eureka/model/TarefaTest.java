package br.com.eureka.model;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TarefaTest {

    private static final ZoneId TIME_ZONE = ZoneId.of("America/Sao_Paulo");

    @Test
    void deveCriarTarefaComDataCriacaoAutomaticaEStatusPendente() {
        Clock clock = Clock.fixed(Instant.parse("2026-06-10T12:00:00Z"), TIME_ZONE);
        Disciplina disciplina = Disciplina.criar("Matematica", AnoLetivo.criar(2026, Aluno.criar("Ana", "ana@escola.com", "ana", "senha123")));

        Tarefa tarefa = Tarefa.criar("Lista 1", LocalDate.of(2026, 6, 10), disciplina, clock);

        assertEquals(LocalDate.of(2026, 6, 10), tarefa.getDataCriacao());
        assertEquals(StatusTarefa.PENDENTE, tarefa.getStatus());
    }

    @Test
    void deveEntregarNoPrazoComoEntregue() {
        Clock clock = Clock.fixed(Instant.parse("2026-06-10T12:00:00Z"), TIME_ZONE);
        Disciplina disciplina = Disciplina.criar("Matematica", AnoLetivo.criar(2026, Aluno.criar("Ana", "ana@escola.com", "ana", "senha123")));
        Tarefa tarefa = Tarefa.criar("Lista 1", LocalDate.of(2026, 6, 12), disciplina, clock);

        tarefa.entregarEm(LocalDate.of(2026, 6, 12));

        assertEquals(LocalDate.of(2026, 6, 12), tarefa.getDataEntrega());
        assertEquals(StatusTarefa.ENTREGUE, tarefa.getStatus());
        assertEquals(SituacaoPrazoTarefa.NO_PRAZO, tarefa.calcularSituacaoPrazo(LocalDate.of(2026, 6, 13)));
    }

    @Test
    void deveEntregarDepoisDoPrazoComoEntregueComAtraso() {
        Clock clock = Clock.fixed(Instant.parse("2026-06-10T12:00:00Z"), TIME_ZONE);
        Disciplina disciplina = Disciplina.criar("Matematica", AnoLetivo.criar(2026, Aluno.criar("Ana", "ana@escola.com", "ana", "senha123")));
        Tarefa tarefa = Tarefa.criar("Lista 1", LocalDate.of(2026, 6, 12), disciplina, clock);

        tarefa.entregarEm(LocalDate.of(2026, 6, 13));

        assertEquals(LocalDate.of(2026, 6, 13), tarefa.getDataEntrega());
        assertEquals(StatusTarefa.ENTREGUE_COM_ATRASO, tarefa.getStatus());
    }

    @Test
    void deveCalcularVencidaParaTarefaPendenteComPrazoPassado() {
        Clock clock = Clock.fixed(Instant.parse("2026-06-10T12:00:00Z"), TIME_ZONE);
        Disciplina disciplina = Disciplina.criar("Matematica", AnoLetivo.criar(2026, Aluno.criar("Ana", "ana@escola.com", "ana", "senha123")));
        Tarefa tarefa = Tarefa.criar("Lista 1", LocalDate.of(2026, 6, 11), disciplina, clock);

        assertEquals(SituacaoPrazoTarefa.VENCIDA, tarefa.calcularSituacaoPrazo(LocalDate.of(2026, 6, 12)));
        assertTrue(tarefa.estaVencida(LocalDate.of(2026, 6, 12)));
        assertEquals(StatusTarefa.PENDENTE, tarefa.getStatus());
    }

    @Test
    void deveImpedirEntregaAntesDaCriacao() {
        Clock clock = Clock.fixed(Instant.parse("2026-06-10T12:00:00Z"), TIME_ZONE);
        Disciplina disciplina = Disciplina.criar("Matematica", AnoLetivo.criar(2026, Aluno.criar("Ana", "ana@escola.com", "ana", "senha123")));
        Tarefa tarefa = Tarefa.criar("Lista 1", LocalDate.of(2026, 6, 12), disciplina, clock);

        assertThrows(IllegalArgumentException.class, () -> tarefa.entregarEm(LocalDate.of(2026, 6, 9)));
    }

    @Test
    void deveImpedirPrazoAnteriorADataCriacao() {
        Clock clock = Clock.fixed(Instant.parse("2026-06-10T12:00:00Z"), TIME_ZONE);
        Disciplina disciplina = Disciplina.criar("Matematica", AnoLetivo.criar(2026, Aluno.criar("Ana", "ana@escola.com", "ana", "senha123")));

        assertThrows(IllegalArgumentException.class, () -> Tarefa.criar("Lista 1", LocalDate.of(2026, 6, 9), disciplina, clock));
    }

    @Test
    void naoDeveReabrirTarefaEntregue() {
        Clock clock = Clock.fixed(Instant.parse("2026-06-10T12:00:00Z"), TIME_ZONE);
        Disciplina disciplina = Disciplina.criar("Matematica", AnoLetivo.criar(2026, Aluno.criar("Ana", "ana@escola.com", "ana", "senha123")));
        Tarefa tarefa = Tarefa.criar("Lista 1", LocalDate.of(2026, 6, 12), disciplina, clock);
        tarefa.entregarEm(LocalDate.of(2026, 6, 12));

        assertThrows(IllegalStateException.class, () -> tarefa.entregarEm(LocalDate.of(2026, 6, 13)));
    }
}
