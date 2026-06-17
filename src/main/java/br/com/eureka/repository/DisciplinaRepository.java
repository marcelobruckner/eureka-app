package br.com.eureka.repository;

import br.com.eureka.model.AnoLetivo;
import br.com.eureka.model.Disciplina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {

    List<Disciplina> findByAnoLetivoAlunoUsuarioAndExcluidoFalseOrderByNomeAsc(String usuario);

    List<Disciplina> findByAnoLetivoIdAndExcluidoFalseOrderByNomeAsc(Long anoLetivoId);

    @Query("""
            select d
            from Disciplina d
            join fetch d.anoLetivo al
            join fetch al.aluno a
            where d.id = :id
              and a.usuario = :usuario
              and d.excluido = false
            """)
    Optional<Disciplina> findByIdAndAnoLetivoAlunoUsuarioAndExcluidoFalse(
            @Param("id") Long id,
            @Param("usuario") String usuario
    );

    boolean existsByAnoLetivoAndNomeIgnoreCaseAndExcluidoFalse(AnoLetivo anoLetivo, String nome);
}
