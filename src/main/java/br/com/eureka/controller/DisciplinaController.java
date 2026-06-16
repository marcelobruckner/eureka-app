package br.com.eureka.controller;

import br.com.eureka.form.DisciplinaForm;
import br.com.eureka.service.AnoLetivoService;
import br.com.eureka.service.DisciplinaService;
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
public class DisciplinaController {

    private final DisciplinaService disciplinaService;
    private final AnoLetivoService anoLetivoService;

    public DisciplinaController(DisciplinaService disciplinaService, AnoLetivoService anoLetivoService) {
        this.disciplinaService = disciplinaService;
        this.anoLetivoService = anoLetivoService;
    }

    @GetMapping("/anos-letivos/{anoLetivoId}/disciplinas")
    public String listarPorAno(
            Authentication authentication,
            @PathVariable Long anoLetivoId,
            Model model
    ) {
        String usuario = authentication.getName();
        model.addAttribute("anoSelecionado", anoLetivoService.obterDoAluno(usuario, anoLetivoId));
        model.addAttribute("disciplinaForm", new DisciplinaForm());
        model.addAttribute("anosLetivos", anoLetivoService.listarDoAluno(usuario));
        model.addAttribute("disciplinas", disciplinaService.listarDoAnoLetivo(usuario, anoLetivoId));
        return "disciplinas";
    }

    @PostMapping("/disciplinas")
    public String cadastrar(
            Authentication authentication,
            @Valid @ModelAttribute("disciplinaForm") DisciplinaForm form,
            BindingResult bindingResult,
            Model model
    ) {
        String usuario = authentication.getName();
        if (bindingResult.hasErrors()) {
            Long anoLetivoId = form.getAnoLetivoId();
            if (anoLetivoId != null) {
                model.addAttribute("anoSelecionado", anoLetivoService.obterDoAluno(usuario, anoLetivoId));
                model.addAttribute("disciplinas", disciplinaService.listarDoAnoLetivo(usuario, anoLetivoId));
            }
            model.addAttribute("anosLetivos", anoLetivoService.listarDoAluno(usuario));
            return "disciplinas";
        }

        return "redirect:/anos-letivos/" + form.getAnoLetivoId() + "/disciplinas";
    }
}
