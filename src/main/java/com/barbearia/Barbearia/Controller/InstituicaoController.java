package com.barbearia.Barbearia.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.barbearia.Barbearia.Model.Instituicao;
import com.barbearia.Barbearia.Model.User;
import com.barbearia.Barbearia.service.InstituicaoService;
import com.barbearia.Barbearia.service.UserService;

import jakarta.validation.Valid;

@Controller
public class InstituicaoController {

    @Autowired
    private InstituicaoService instituicaoService;

    @Autowired
    private UserService userService;

    @GetMapping("/instituicao")
    public String getInstituicao(Model model,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String success) {
        if (userDetails != null) {
            User user = userService.findByEmail(userDetails.getUsername());
            String primeiroNome = user.getName().split(" ")[0];
            model.addAttribute("userName", primeiroNome);
            model.addAttribute("isAuthenticated", true);
        } else {
            model.addAttribute("isAuthenticated", false);
            model.addAttribute("userName", "");
        }

        model.addAttribute("instituicao", new Instituicao());

        // PASSA PARA O THYMELEAF SE DEVE MOSTRAR O MODAL
        if ("true".equals(success)) {
            model.addAttribute("showSuccessModal", true);
        }

        return "HTML/instituicao";
    }

    @GetMapping("/instituicao/inscritas")
    public String getInstituicoesInscritas(Model model) {
        model.addAttribute("instituicoes", instituicaoService.getAllInstituicoes());
        return "HTML/instituicoesInscritas";
    }

    @PostMapping("/instituicao/save")
    public String saveInstituicao(@ModelAttribute Instituicao instituicao, Model model,
            @AuthenticationPrincipal UserDetails userDetails) {
        instituicaoService.saveInstituicao(instituicao);

        if (userDetails != null) {
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("userName", userDetails.getUsername());
        } else {
            model.addAttribute("isAuthenticated", false);
            model.addAttribute("userName", "");
        }

        return "redirect:/instituicao?success=true";
    }

}