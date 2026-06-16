package br.com.eureka.service;

import br.com.eureka.model.Aluno;
import br.com.eureka.repository.AlunoRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlunoUsuarioDetailsService implements UserDetailsService {

    private final AlunoRepository alunoRepository;

    public AlunoUsuarioDetailsService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Aluno aluno = alunoRepository.findByUsuarioAndExcluidoFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("Aluno nao encontrado"));

        return User.withUsername(aluno.getUsuario())
                .password(aluno.getSenha())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_ALUNO")))
                .build();
    }
}
