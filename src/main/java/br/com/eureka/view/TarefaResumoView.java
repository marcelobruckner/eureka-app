package br.com.eureka.view;

import br.com.eureka.model.SituacaoPrazoTarefa;
import br.com.eureka.model.StatusTarefa;

import java.time.LocalDate;

public record TarefaResumoView(
        Long id,
        String nome,
        LocalDate dataCriacao,
        LocalDate dataPrevistaEntrega,
        LocalDate dataEntrega,
        StatusTarefa status,
        SituacaoPrazoTarefa situacaoPrazo
) {
    public boolean isVencida() {
        return situacaoPrazo == SituacaoPrazoTarefa.VENCIDA;
    }
}
