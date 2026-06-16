package br.com.eureka.form;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class AnoLetivoForm {

    @NotNull(message = "Ano e obrigatorio")
    @Positive(message = "Ano deve ser maior que zero")
    private Integer ano;

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }
}
