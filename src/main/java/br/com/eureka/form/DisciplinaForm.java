package br.com.eureka.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class DisciplinaForm {

    @NotBlank(message = "Nome e obrigatorio")
    private String nome;

    @NotNull(message = "Ano letivo e obrigatorio")
    @Positive(message = "Ano letivo invalido")
    private Long anoLetivoId;

    private Long anoSelecionadoId;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getAnoLetivoId() {
        return anoLetivoId;
    }

    public void setAnoLetivoId(Long anoLetivoId) {
        this.anoLetivoId = anoLetivoId;
    }

    public Long getAnoSelecionadoId() {
        return anoSelecionadoId;
    }

    public void setAnoSelecionadoId(Long anoSelecionadoId) {
        this.anoSelecionadoId = anoSelecionadoId;
    }
}
