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
@Table(name = "disciplina")
public class Disciplina extends RegistroExcluivel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ano_letivo_id", nullable = false)
    private AnoLetivo anoLetivo;

    protected Disciplina() {
    }

    private Disciplina(String nome, AnoLetivo anoLetivo) {
        this.nome = textoObrigatorio(nome, "nome");
        this.anoLetivo = validarAnoLetivo(anoLetivo);
    }

    public static Disciplina criar(String nome, AnoLetivo anoLetivo) {
        return new Disciplina(nome, anoLetivo);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public AnoLetivo getAnoLetivo() {
        return anoLetivo;
    }

    public void atualizarNome(String nome) {
        this.nome = textoObrigatorio(nome, "nome");
    }

    private static AnoLetivo validarAnoLetivo(AnoLetivo anoLetivo) {
        if (anoLetivo == null) {
            throw new IllegalArgumentException("anoLetivo e obrigatorio");
        }
        return anoLetivo;
    }
}
