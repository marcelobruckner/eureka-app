package br.com.eureka.repository;

import br.com.eureka.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    Optional<Aluno> findByUsuarioAndExcluidoFalse(String usuario);

    Optional<Aluno> findByEmailAndExcluidoFalse(String email);

    boolean existsByUsuarioAndExcluidoFalse(String usuario);

    boolean existsByEmailAndExcluidoFalse(String email);
}
