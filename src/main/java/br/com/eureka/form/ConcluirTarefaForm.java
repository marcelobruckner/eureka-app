package br.com.eureka.form;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class ConcluirTarefaForm {

    @NotNull(message = "Data de entrega e obrigatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataEntrega;

    public LocalDate getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(LocalDate dataEntrega) {
        this.dataEntrega = dataEntrega;
    }
}
