package br.com.eureka.service;

import br.com.eureka.form.CadastroAlunoForm;
import br.com.eureka.model.Aluno;
import br.com.eureka.repository.AlunoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final PasswordEncoder passwordEncoder;

    public AlunoService(AlunoRepository alunoRepository, PasswordEncoder passwordEncoder) {
        this.alunoRepository = alunoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Aluno cadastrar(CadastroAlunoForm form) {
        validarDisponibilidade(form.getUsuario(), form.getEmail());

        Aluno aluno = Aluno.criar(
                normalizar(form.getNome()),
                normalizar(form.getEmail()),
                normalizar(form.getUsuario()),
                passwordEncoder.encode(form.getSenha())
        );
        return alunoRepository.save(aluno);
    }

    private void validarDisponibilidade(String usuario, String email) {
        String usuarioNormalizado = normalizar(usuario);
        String emailNormalizado = normalizar(email);

        if (alunoRepository.existsByUsuarioAndExcluidoFalse(usuarioNormalizado)) {
            throw new CadastroAlunoException("Usuario ja cadastrado");
        }
        if (alunoRepository.existsByEmailAndExcluidoFalse(emailNormalizado)) {
            throw new CadastroAlunoException("Email ja cadastrado");
        }
    }

    private String normalizar(String valor) {
        return valor == null ? null : valor.trim();
    }
}
