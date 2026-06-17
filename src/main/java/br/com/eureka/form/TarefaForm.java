package br.com.eureka.form;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class TarefaForm {

    @NotBlank(message = "Nome e obrigatorio")
    private String nome;

    @NotNull(message = "Data prevista de entrega e obrigatoria")
    @FutureOrPresent(message = "Data prevista de entrega nao pode ser anterior a hoje")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataPrevistaEntrega;

    @NotNull(message = "Disciplina e obrigatoria")
    @Positive(message = "Disciplina invalida")
    private Long disciplinaId;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataPrevistaEntrega() {
        return dataPrevistaEntrega;
    }

    public void setDataPrevistaEntrega(LocalDate dataPrevistaEntrega) {
        this.dataPrevistaEntrega = dataPrevistaEntrega;
    }

    public Long getDisciplinaId() {
        return disciplinaId;
    }

    public void setDisciplinaId(Long disciplinaId) {
        this.disciplinaId = disciplinaId;
    }
}
