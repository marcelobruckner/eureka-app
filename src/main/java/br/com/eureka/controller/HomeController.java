package br.com.eureka.controller;

import br.com.eureka.model.AnoLetivo;
import br.com.eureka.model.FiltroSituacaoTarefa;
import br.com.eureka.view.DisciplinaTarefasView;
import br.com.eureka.service.AnoLetivoService;
import br.com.eureka.service.DisciplinaService;
import br.com.eureka.service.TarefaService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private final AnoLetivoService anoLetivoService;
    private final DisciplinaService disciplinaService;
    private final TarefaService tarefaService;

    public HomeController(
            AnoLetivoService anoLetivoService,
            DisciplinaService disciplinaService,
            TarefaService tarefaService
    ) {
        this.anoLetivoService = anoLetivoService;
        this.disciplinaService = disciplinaService;
        this.tarefaService = tarefaService;
    }

    @GetMapping("/")
    public String raiz() {
        return "redirect:/inicio";
    }

    @GetMapping("/inicio")
    public String inicio(
            Authentication authentication,
            @RequestParam(name = "anoLetivoId", required = false) Long anoLetivoId,
            @RequestParam(name = "situacaoTarefa", required = false, defaultValue = "TODAS")
            FiltroSituacaoTarefa filtroSituacao,
            Model model
    ) {
        String usuario = authentication.getName();
        List<AnoLetivo> anosLetivos = anoLetivoService.listarDoAluno(usuario);
        AnoLetivo anoSelecionado = selecionarAno(anoLetivoId, anosLetivos);
        List<DisciplinaTarefasView> tarefasPorDisciplina = anoSelecionado != null
                ? tarefaService.listarAgrupadasPorAnoLetivo(usuario, anoSelecionado.getId(), filtroSituacao)
                : List.of();
        List<br.com.eureka.model.Disciplina> disciplinas = anoSelecionado != null
                ? disciplinaService.listarDoAnoLetivo(usuario, anoSelecionado.getId())
                : List.of();
        int quantidadeTarefas = tarefasPorDisciplina.stream().mapToInt(bloco -> bloco.tarefas().size()).sum();

        model.addAttribute("usuarioLogado", usuario);
        model.addAttribute("anosLetivos", anosLetivos);
        model.addAttribute("anoSelecionado", anoSelecionado);
        model.addAttribute("filtroSituacao", filtroSituacao);
        model.addAttribute("filtrosSituacao", List.of(FiltroSituacaoTarefa.values()));
        model.addAttribute("quantidadeAnosLetivos", anosLetivos.size());
        model.addAttribute("quantidadeDisciplinas", disciplinas.size());
        model.addAttribute("quantidadeTarefas", quantidadeTarefas);
        model.addAttribute("disciplinas", disciplinas);
        model.addAttribute("tarefasPorDisciplina", tarefasPorDisciplina);
        model.addAttribute("temTarefas", tarefasPorDisciplina.stream().anyMatch(bloco -> !bloco.tarefas().isEmpty()));
        return "inicio";
    }

    private AnoLetivo selecionarAno(Long anoLetivoId, List<AnoLetivo> anosLetivos) {
        if (anosLetivos.isEmpty()) {
            return null;
        }
        if (anoLetivoId != null) {
            return anosLetivos.stream()
                    .filter(anoLetivo -> anoLetivo.getId().equals(anoLetivoId))
                    .findFirst()
                    .orElse(anosLetivos.get(0));
        }
        return anosLetivos.get(0);
    }
}
