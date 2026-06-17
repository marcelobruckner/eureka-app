package br.com.eureka.model;

public enum FiltroSituacaoTarefa {
    TODAS("Todas"),
    PENDENTE("Pendente"),
    VENCIDA("Vencida"),
    ENTREGUE("Entregue"),
    ENTREGUE_COM_ATRASO("Entregue com atraso");

    private final String descricao;

    FiltroSituacaoTarefa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
