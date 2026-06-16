package br.com.eureka.controller;

import br.com.eureka.form.CadastroAlunoForm;
import br.com.eureka.service.AlunoService;
import br.com.eureka.service.CadastroAlunoException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AutenticacaoController {

    private final AlunoService alunoService;

    public AutenticacaoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("cadastroAlunoForm", new CadastroAlunoForm());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(
            @Valid @ModelAttribute("cadastroAlunoForm") CadastroAlunoForm form,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "registro";
        }

        try {
            alunoService.cadastrar(form);
            return "redirect:/login?cadastro";
        } catch (CadastroAlunoException ex) {
            model.addAttribute("erroGeral", ex.getMessage());
            return "registro";
        }
    }
}
