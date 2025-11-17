package com.barbearia.Barbearia.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.barbearia.Barbearia.Model.Instituicao;
import com.barbearia.Barbearia.service.InstituicaoService;

import jakarta.validation.Valid;

@Controller
public class InstituicaoController {

    @Autowired
    private InstituicaoService instituicaoService;
    
    @GetMapping("/instituicao")
    public String getInstituicao() {
        return "html/instituicao";
    }

    @GetMapping("/instituicao/create")
    public String create(Model model) {
        model.addAttribute("instituicao", new Instituicao());
        return "instituicao/form";
    }

    @PostMapping("/instituicao/save")
    public String save(@ModelAttribute @Valid Instituicao instituicao, BindingResult result, Model model) {
        System.out.println(instituicao);
        if (result.hasErrors()) {
            model.addAttribute("instituicao", instituicao);
            return "instituicao/form";
        }

        instituicaoService.saveInstituicao(instituicao);
        return "redirect:/instituicao";
    }

}
