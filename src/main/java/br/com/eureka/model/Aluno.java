package br.com.eureka.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "aluno")
public class Aluno extends RegistroExcluivel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 100)
    private String usuario;

    @Column(nullable = false, length = 255)
    private String senha;

    protected Aluno() {
    }

    private Aluno(String nome, String email, String usuario, String senha) {
        this.nome = textoObrigatorio(nome, "nome");
        this.email = textoObrigatorio(email, "email");
        this.usuario = textoObrigatorio(usuario, "usuario");
        this.senha = textoObrigatorio(senha, "senha");
    }

    public static Aluno criar(String nome, String email, String usuario, String senha) {
        return new Aluno(nome, email, usuario, senha);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void atualizarDados(String nome, String email, String usuario, String senha) {
        this.nome = textoObrigatorio(nome, "nome");
        this.email = textoObrigatorio(email, "email");
        this.usuario = textoObrigatorio(usuario, "usuario");
        this.senha = textoObrigatorio(senha, "senha");
    }
}
