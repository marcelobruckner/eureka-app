package br.com.eureka.controller;

import br.com.eureka.form.AnoLetivoForm;
import br.com.eureka.service.AnoLetivoService;
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
public class AnoLetivoController {

    private final AnoLetivoService anoLetivoService;

    public AnoLetivoController(AnoLetivoService anoLetivoService) {
        this.anoLetivoService = anoLetivoService;
    }

    @GetMapping("/anos-letivos")
    public String listar(Authentication authentication, Model model) {
        model.addAttribute("anoLetivoForm", new AnoLetivoForm());
        model.addAttribute("anosLetivos", anoLetivoService.listarDoAluno(authentication.getName()));
        return "anos-letivos";
    }

    @PostMapping("/anos-letivos")
    public String cadastrar(
            Authentication authentication,
            @Valid @ModelAttribute("anoLetivoForm") AnoLetivoForm form,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("anosLetivos", anoLetivoService.listarDoAluno(authentication.getName()));
            return "anos-letivos";
        }

        anoLetivoService.cadastrar(authentication.getName(), form);
        return "redirect:/anos-letivos";
    }

    @PostMapping("/anos-letivos/{anoLetivoId}/excluir")
    public String excluir(
            Authentication authentication,
            @PathVariable Long anoLetivoId,
            Model model
    ) {
        String usuario = authentication.getName();
        try {
            anoLetivoService.excluir(usuario, anoLetivoId);
            return "redirect:/anos-letivos";
        } catch (IllegalArgumentException e) {
            model.addAttribute("anoLetivoForm", new AnoLetivoForm());
            model.addAttribute("anosLetivos", anoLetivoService.listarDoAluno(usuario));
            model.addAttribute("mensagemErro", e.getMessage());
            return "anos-letivos";
        }
    }
}
