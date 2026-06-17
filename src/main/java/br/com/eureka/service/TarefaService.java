package br.com.eureka.service;

import br.com.eureka.form.ConcluirTarefaForm;
import br.com.eureka.form.TarefaForm;
import br.com.eureka.model.Disciplina;
import br.com.eureka.model.Tarefa;
import br.com.eureka.repository.TarefaRepository;
import br.com.eureka.view.DisciplinaTarefasView;
import br.com.eureka.view.TarefaResumoView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class TarefaService {

    private final TarefaRepository tarefaRepository;
    private final DisciplinaService disciplinaService;
    private final Clock clock;

    public TarefaService(TarefaRepository tarefaRepository, DisciplinaService disciplinaService, Clock clock) {
        this.tarefaRepository = tarefaRepository;
        this.disciplinaService = disciplinaService;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<TarefaResumoView> listarDaDisciplina(String usuario, Long disciplinaId) {
        LocalDate referencia = hoje();
        return tarefaRepository.findByDisciplinaAndUsuarioOrderByDataPrevistaEntregaAscNomeAsc(usuario, disciplinaId)
                .stream()
                .map(tarefa -> toResumo(tarefa, referencia))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DisciplinaTarefasView> listarAgrupadasPorAnoLetivo(String usuario, Long anoLetivoId) {
        LocalDate referencia = hoje();
        List<Disciplina> disciplinas = disciplinaService.listarDoAnoLetivo(usuario, anoLetivoId);
        List<Tarefa> tarefas = tarefaRepository.findByAnoLetivoAndUsuarioOrderByDisciplinaNomeAscDataPrevistaEntregaAscNomeAsc(usuario, anoLetivoId);

        Map<Long, List<TarefaResumoView>> agrupadas = new LinkedHashMap<>();
        for (Disciplina disciplina : disciplinas) {
            agrupadas.put(disciplina.getId(), new java.util.ArrayList<>());
        }
        for (Tarefa tarefa : tarefas) {
            agrupadas.get(tarefa.getDisciplina().getId()).add(toResumo(tarefa, referencia));
        }

        return disciplinas.stream()
                .map(disciplina -> new DisciplinaTarefasView(
                        disciplina.getId(),
                        disciplina.getNome(),
                        agrupadas.getOrDefault(disciplina.getId(), List.of())
                ))
                .toList();
    }

    @Transactional
    public Tarefa cadastrar(String usuario, TarefaForm form) {
        Disciplina disciplina = disciplinaService.obterDoAluno(usuario, form.getDisciplinaId());
        return tarefaRepository.save(Tarefa.criar(
                form.getNome().trim(),
                form.getDataPrevistaEntrega(),
                disciplina,
                clock
        ));
    }

    @Transactional(readOnly = true)
    public Tarefa obterDoUsuario(String usuario, Long tarefaId) {
        return tarefaRepository.findByIdAndUsuario(usuario, tarefaId)
                .orElseThrow(() -> new IllegalArgumentException("Tarefa nao encontrada"));
    }

    @Transactional
    public Tarefa concluir(String usuario, Long tarefaId, ConcluirTarefaForm form) {
        Tarefa tarefa = obterDoUsuario(usuario, tarefaId);
        tarefa.entregarEm(form.getDataEntrega());
        return tarefa;
    }

    private TarefaResumoView toResumo(Tarefa tarefa, LocalDate referencia) {
        return new TarefaResumoView(
                tarefa.getId(),
                tarefa.getNome(),
                tarefa.getDataCriacao(),
                tarefa.getDataPrevistaEntrega(),
                tarefa.getDataEntrega(),
                tarefa.getStatus(),
                tarefa.calcularSituacaoPrazo(referencia)
        );
    }

    private LocalDate hoje() {
        return LocalDate.now(clock);
    }
}
