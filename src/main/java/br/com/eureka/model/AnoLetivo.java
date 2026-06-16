package br.com.eureka.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ano_letivo")
public class AnoLetivo extends RegistroExcluivel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer ano;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    protected AnoLetivo() {
    }

    private AnoLetivo(Integer ano, Aluno aluno) {
        this.ano = validarAno(ano);
        this.aluno = validarAluno(aluno);
    }

    public static AnoLetivo criar(Integer ano, Aluno aluno) {
        return new AnoLetivo(ano, aluno);
    }

    public Long getId() {
        return id;
    }

    public Integer getAno() {
        return ano;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void atualizarAno(Integer ano) {
        this.ano = validarAno(ano);
    }

    private static Integer validarAno(Integer ano) {
        if (ano == null) {
            throw new IllegalArgumentException("ano e obrigatorio");
        }
        return ano;
    }

    private static Aluno validarAluno(Aluno aluno) {
        if (aluno == null) {
            throw new IllegalArgumentException("aluno e obrigatorio");
        }
        return aluno;
    }
}
