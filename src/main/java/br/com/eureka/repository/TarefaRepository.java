package br.com.eureka.repository;

import br.com.eureka.model.Tarefa;
import br.com.eureka.model.Disciplina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    @Query("""
            select t
            from Tarefa t
            join fetch t.disciplina d
            join fetch d.anoLetivo al
            where al.aluno.usuario = :usuario
              and d.id = :disciplinaId
              and t.excluido = false
            order by t.dataCriacao asc, t.id asc
            """)
    List<Tarefa> findByDisciplinaAndUsuarioOrderByDataPrevistaEntregaAscNomeAsc(
            @Param("usuario") String usuario,
            @Param("disciplinaId") Long disciplinaId
    );

    @Query("""
            select t
            from Tarefa t
            join fetch t.disciplina d
            join fetch d.anoLetivo al
            where al.aluno.usuario = :usuario
              and al.id = :anoLetivoId
              and t.excluido = false
            order by d.nome asc, t.dataCriacao asc, t.id asc
            """)
    List<Tarefa> findByAnoLetivoAndUsuarioOrderByDisciplinaNomeAscDataPrevistaEntregaAscNomeAsc(
            @Param("usuario") String usuario,
            @Param("anoLetivoId") Long anoLetivoId
    );

    @Query("""
            select t
            from Tarefa t
            join fetch t.disciplina d
            join fetch d.anoLetivo al
            where al.aluno.usuario = :usuario
              and t.id = :tarefaId
              and t.excluido = false
            """)
    Optional<Tarefa> findByIdAndUsuario(
            @Param("usuario") String usuario,
            @Param("tarefaId") Long tarefaId
    );

    boolean existsByDisciplinaAndExcluidoFalse(Disciplina disciplina);
}
