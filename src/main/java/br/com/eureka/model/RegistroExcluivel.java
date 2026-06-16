package br.com.eureka.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

@MappedSuperclass
public abstract class RegistroExcluivel {

    @Column(name = "excluido", nullable = false)
    private boolean excluido;

    @Column(name = "excluido_em")
    private Instant excluidoEm;

    @Column(name = "excluido_por", length = 150)
    private String excluidoPor;

    public boolean isExcluido() {
        return excluido;
    }

    public Instant getExcluidoEm() {
        return excluidoEm;
    }

    public String getExcluidoPor() {
        return excluidoPor;
    }

    public void excluir(String excluidoPor, Clock clock) {
        if (excluido) {
            return;
        }
        this.excluido = true;
        this.excluidoEm = Instant.now(Objects.requireNonNull(clock, "clock nao pode ser nulo"));
        this.excluidoPor = textoObrigatorio(excluidoPor, "excluidoPor");
    }

    protected static String textoObrigatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " e obrigatorio");
        }
        return valor;
    }
}
