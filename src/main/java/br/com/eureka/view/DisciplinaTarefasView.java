package br.com.eureka.view;

import java.util.List;

public record DisciplinaTarefasView(
        Long disciplinaId,
        String disciplinaNome,
        List<TarefaResumoView> tarefas
) {
}
