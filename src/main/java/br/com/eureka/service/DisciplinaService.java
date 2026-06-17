package br.com.eureka.service;

import br.com.eureka.form.DisciplinaForm;
import br.com.eureka.model.AnoLetivo;
import br.com.eureka.model.Disciplina;
import br.com.eureka.repository.DisciplinaRepository;
import br.com.eureka.repository.TarefaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;

@Service
public class DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;
    private final AnoLetivoService anoLetivoService;
    private final TarefaRepository tarefaRepository;
    private final Clock clock;

    public DisciplinaService(
            DisciplinaRepository disciplinaRepository,
            AnoLetivoService anoLetivoService,
            TarefaRepository tarefaRepository,
            Clock clock
    ) {
        this.disciplinaRepository = disciplinaRepository;
        this.anoLetivoService = anoLetivoService;
        this.tarefaRepository = tarefaRepository;
        this.clock = clock;
    }

    public List<Disciplina> listarDoAluno(String usuario) {
        return disciplinaRepository.findByAnoLetivoAlunoUsuarioAndExcluidoFalseOrderByNomeAsc(usuario);
    }

    public List<Disciplina> listarDoAnoLetivo(String usuario, Long anoLetivoId) {
        anoLetivoService.obterDoAluno(usuario, anoLetivoId);
        return disciplinaRepository.findByAnoLetivoIdAndExcluidoFalseOrderByNomeAsc(anoLetivoId);
    }

    public Disciplina obterDoAluno(String usuario, Long disciplinaId) {
        return disciplinaRepository.findByIdAndAnoLetivoAlunoUsuarioAndExcluidoFalse(disciplinaId, usuario)
                .orElseThrow(() -> new IllegalArgumentException("Disciplina nao encontrada"));
    }

    @Transactional
    public Disciplina cadastrar(String usuario, DisciplinaForm form) {
        AnoLetivo anoLetivo = anoLetivoService.obterDoAluno(usuario, form.getAnoLetivoId());
        String nome = form.getNome().trim();
        if (disciplinaRepository.existsByAnoLetivoAndNomeIgnoreCaseAndExcluidoFalse(anoLetivo, nome)) {
            throw new IllegalArgumentException("Disciplina ja cadastrada");
        }

        return disciplinaRepository.save(Disciplina.criar(nome, anoLetivo));
    }

    @Transactional
    public Disciplina excluir(String usuario, Long disciplinaId) {
        Disciplina disciplina = obterDoAluno(usuario, disciplinaId);
        if (tarefaRepository.existsByDisciplinaAndExcluidoFalse(disciplina)) {
            throw new IllegalArgumentException("Disciplina possui tarefas vinculadas");
        }

        disciplina.excluir(usuario, clock);
        return disciplinaRepository.save(disciplina);
    }
}
