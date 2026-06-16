package br.com.eureka.repository;

import br.com.eureka.model.AnoLetivo;
import br.com.eureka.model.Disciplina;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {

    List<Disciplina> findByAnoLetivoAlunoUsuarioAndExcluidoFalseOrderByNomeAsc(String usuario);

    List<Disciplina> findByAnoLetivoIdAndExcluidoFalseOrderByNomeAsc(Long anoLetivoId);

    Optional<Disciplina> findByIdAndAnoLetivoAlunoUsuarioAndExcluidoFalse(Long id, String usuario);

    boolean existsByAnoLetivoAndNomeIgnoreCaseAndExcluidoFalse(AnoLetivo anoLetivo, String nome);
}
