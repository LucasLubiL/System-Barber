package com.barbearia.Barbearia.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.barbearia.Barbearia.Model.Barbeiro;
import com.barbearia.Barbearia.service.BarbeiroService;

import jakarta.servlet.http.HttpSession;

@Controller
public class BarbeiroController {

    @Autowired
    private BarbeiroService barbeiroService;

    // CRIAR BARBEIRO
    @PostMapping("/createBarbeiro")
    public String criarBarbeiro(
            @RequestParam String nomeCompleto,
            @RequestParam String especialidade,
            @RequestParam String telefone,
            @RequestParam String email,
            @RequestParam String senha,
            @RequestParam String confirmarSenha,
            HttpSession session) {
        try {
            barbeiroService.createBarbeiroFromParams(nomeCompleto, especialidade, telefone, email, senha,
                    confirmarSenha);
            return "redirect:/agendamento?barbeiroCriado=true";
        } catch (RuntimeException e) {
            session.setAttribute("erroCriarBarbeiro", e.getMessage());
            return "redirect:/agendamento?erroCriar=true";
        }
    }

    // EXCLUIR BARBEIRO
    @GetMapping("/deleteBarbeiro")
    public String excluirBarbeiro(@RequestParam Long id) {
        try {
            barbeiroService.deleteBarbeiro(id);
            return "redirect:/agendamento?barbeiroExcluido=true";
        } catch (RuntimeException e) {
            return "redirect:/agendamento?erroExcluir=true";
        }
    }
}