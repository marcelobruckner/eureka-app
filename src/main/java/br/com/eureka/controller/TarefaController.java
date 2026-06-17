package br.com.eureka.controller;

import br.com.eureka.form.ConcluirTarefaForm;
import br.com.eureka.form.TarefaForm;
import br.com.eureka.model.Tarefa;
import br.com.eureka.model.StatusTarefa;
import br.com.eureka.service.DisciplinaService;
import br.com.eureka.service.TarefaService;
import br.com.eureka.view.TarefaResumoView;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TarefaController {

    private final TarefaService tarefaService;
    private final DisciplinaService disciplinaService;

    public TarefaController(TarefaService tarefaService, DisciplinaService disciplinaService) {
        this.tarefaService = tarefaService;
        this.disciplinaService = disciplinaService;
    }

    @GetMapping("/disciplinas/{disciplinaId}/tarefas")
    public String listar(
            Authentication authentication,
            @PathVariable Long disciplinaId,
            Model model
    ) {
        String usuario = authentication.getName();
        carregarTela(usuario, disciplinaId, model);
        return "tarefas";
    }

    @PostMapping("/tarefas")
    public String cadastrar(
            Authentication authentication,
            @Valid @ModelAttribute("tarefaForm") TarefaForm form,
            BindingResult bindingResult,
            Model model
    ) {
        String usuario = authentication.getName();
        if (bindingResult.hasErrors()) {
            model.addAttribute("tarefaForm", form);
            carregarDadosTela(usuario, form.getDisciplinaId(), model);
            return "tarefas";
        }

        tarefaService.cadastrar(usuario, form);
        return "redirect:/disciplinas/" + form.getDisciplinaId() + "/tarefas";
    }

    @GetMapping("/tarefas/{tarefaId}/concluir")
    public String formularioConcluir(
            Authentication authentication,
            @PathVariable Long tarefaId,
            Model model
    ) {
        String usuario = authentication.getName();
        Tarefa tarefa = tarefaService.obterDoUsuario(usuario, tarefaId);
        if (tarefa.getStatus() != StatusTarefa.PENDENTE) {
            return "redirect:/disciplinas/" + tarefa.getDisciplina().getId() + "/tarefas";
        }
        model.addAttribute("tarefa", tarefa);
        model.addAttribute("concluirTarefaForm", new ConcluirTarefaForm());
        return "concluir-tarefa";
    }

    @PostMapping("/tarefas/{tarefaId}/concluir")
    public String concluir(
            Authentication authentication,
            @PathVariable Long tarefaId,
            @Valid @ModelAttribute("concluirTarefaForm") ConcluirTarefaForm form,
            BindingResult bindingResult,
            Model model
    ) {
        String usuario = authentication.getName();
        Tarefa tarefa = tarefaService.obterDoUsuario(usuario, tarefaId);
        if (tarefa.getStatus() != StatusTarefa.PENDENTE) {
            return "redirect:/disciplinas/" + tarefa.getDisciplina().getId() + "/tarefas";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("tarefa", tarefa);
            return "concluir-tarefa";
        }

        tarefaService.concluir(usuario, tarefaId, form);
        return "redirect:/disciplinas/" + tarefa.getDisciplina().getId() + "/tarefas";
    }

    private void carregarTela(String usuario, Long disciplinaId, Model model) {
        carregarDadosTela(usuario, disciplinaId, model);
        model.addAttribute("tarefaForm", criarForm(disciplinaId));
    }

    private void carregarDadosTela(String usuario, Long disciplinaId, Model model) {
        var disciplina = disciplinaService.obterDoAluno(usuario, disciplinaId);
        java.util.List<TarefaResumoView> tarefas = tarefaService.listarDaDisciplina(usuario, disciplinaId);
        model.addAttribute("disciplina", disciplina);
        model.addAttribute("tarefas", tarefas);
        model.addAttribute("quantidadeTarefas", tarefas.size());
        model.addAttribute("quantidadePendentes", tarefas.stream().filter(TarefaResumoView::isPendenteNoPrazo).count()
                + tarefas.stream().filter(TarefaResumoView::isPendenteVencida).count());
        model.addAttribute("quantidadeEntregues", tarefas.stream().filter(TarefaResumoView::isEntregue).count());
        model.addAttribute("quantidadeAtrasadas", tarefas.stream().filter(TarefaResumoView::isEntregueComAtraso).count());
    }

    private TarefaForm criarForm(Long disciplinaId) {
        TarefaForm form = new TarefaForm();
        form.setDisciplinaId(disciplinaId);
        return form;
    }
}
