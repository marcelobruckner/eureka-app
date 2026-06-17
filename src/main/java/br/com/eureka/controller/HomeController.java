package br.com.eureka.controller;

import br.com.eureka.model.AnoLetivo;
import br.com.eureka.service.AnoLetivoService;
import br.com.eureka.service.DisciplinaService;
import br.com.eureka.service.TarefaService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Clock;
import java.time.LocalDate;

@Controller
public class HomeController {

    private final AnoLetivoService anoLetivoService;
    private final DisciplinaService disciplinaService;
    private final TarefaService tarefaService;
    private final Clock clock;

    public HomeController(
            AnoLetivoService anoLetivoService,
            DisciplinaService disciplinaService,
            TarefaService tarefaService,
            Clock clock
    ) {
        this.anoLetivoService = anoLetivoService;
        this.disciplinaService = disciplinaService;
        this.tarefaService = tarefaService;
        this.clock = clock;
    }

    @GetMapping("/")
    public String raiz() {
        return "redirect:/inicio";
    }

    @GetMapping("/inicio")
    public String inicio(
            Authentication authentication,
            @RequestParam(name = "anoLetivoId", required = false) Long anoLetivoId,
            Model model
    ) {
        String usuario = authentication.getName();
        var anosLetivos = anoLetivoService.listarDoAluno(usuario);
        AnoLetivo anoSelecionado = selecionarAno(anoLetivoId, anosLetivos);

        model.addAttribute("usuarioLogado", usuario);
        model.addAttribute("anosLetivos", anosLetivos);
        model.addAttribute("anoSelecionado", anoSelecionado);
        model.addAttribute("disciplinas", anoSelecionado != null
                ? disciplinaService.listarDoAnoLetivo(usuario, anoSelecionado.getId())
                : java.util.List.of());
        model.addAttribute("tarefasPorDisciplina", anoSelecionado != null
                ? tarefaService.listarAgrupadasPorAnoLetivo(usuario, anoSelecionado.getId())
                : java.util.List.of());
        model.addAttribute("dataReferencia", LocalDate.now(clock));
        return "inicio";
    }

    private AnoLetivo selecionarAno(Long anoLetivoId, java.util.List<AnoLetivo> anosLetivos) {
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
