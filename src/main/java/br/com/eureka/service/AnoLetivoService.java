package br.com.eureka.service;

import br.com.eureka.form.AnoLetivoForm;
import br.com.eureka.model.Aluno;
import br.com.eureka.model.AnoLetivo;
import br.com.eureka.repository.AlunoRepository;
import br.com.eureka.repository.AnoLetivoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnoLetivoService {

    private final AnoLetivoRepository anoLetivoRepository;
    private final AlunoRepository alunoRepository;

    public AnoLetivoService(AnoLetivoRepository anoLetivoRepository, AlunoRepository alunoRepository) {
        this.anoLetivoRepository = anoLetivoRepository;
        this.alunoRepository = alunoRepository;
    }

    public List<AnoLetivo> listarDoAluno(String usuario) {
        return anoLetivoRepository.findByAlunoUsuarioAndExcluidoFalseOrderByAnoDesc(usuario);
    }

    public AnoLetivo obterDoAluno(String usuario, Long id) {
        return anoLetivoRepository.findByIdAndAlunoUsuarioAndExcluidoFalse(id, usuario)
                .orElseThrow(() -> new IllegalArgumentException("Ano letivo nao encontrado"));
    }

    @Transactional
    public AnoLetivo cadastrar(String usuario, AnoLetivoForm form) {
        Integer ano = form.getAno();
        if (anoLetivoRepository.existsByAlunoUsuarioAndAnoAndExcluidoFalse(usuario, ano)) {
            throw new IllegalArgumentException("Ano letivo ja cadastrado");
        }

        Aluno aluno = alunoRepository.findByUsuarioAndExcluidoFalse(usuario)
                .orElseThrow(() -> new IllegalArgumentException("Aluno nao encontrado"));

        return anoLetivoRepository.save(AnoLetivo.criar(ano, aluno));
    }
}
