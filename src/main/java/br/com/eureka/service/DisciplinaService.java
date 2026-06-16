package br.com.eureka.service;

import br.com.eureka.form.DisciplinaForm;
import br.com.eureka.model.AnoLetivo;
import br.com.eureka.model.Disciplina;
import br.com.eureka.repository.DisciplinaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;
    private final AnoLetivoService anoLetivoService;

    public DisciplinaService(DisciplinaRepository disciplinaRepository, AnoLetivoService anoLetivoService) {
        this.disciplinaRepository = disciplinaRepository;
        this.anoLetivoService = anoLetivoService;
    }

    public List<Disciplina> listarDoAluno(String usuario) {
        return disciplinaRepository.findByAnoLetivoAlunoUsuarioAndExcluidoFalseOrderByNomeAsc(usuario);
    }

    public List<Disciplina> listarDoAnoLetivo(String usuario, Long anoLetivoId) {
        anoLetivoService.obterDoAluno(usuario, anoLetivoId);
        return disciplinaRepository.findByAnoLetivoIdAndExcluidoFalseOrderByNomeAsc(anoLetivoId);
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
}
