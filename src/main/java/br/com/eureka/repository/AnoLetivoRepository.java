package br.com.eureka.repository;

import br.com.eureka.model.AnoLetivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnoLetivoRepository extends JpaRepository<AnoLetivo, Long> {

    List<AnoLetivo> findByAlunoUsuarioAndExcluidoFalseOrderByAnoDesc(String usuario);

    Optional<AnoLetivo> findByIdAndAlunoUsuarioAndExcluidoFalse(Long id, String usuario);

    boolean existsByAlunoUsuarioAndAnoAndExcluidoFalse(String usuario, Integer ano);
}
