package com.barbearia.Barbearia.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import com.barbearia.Barbearia.Model.Servico;
import com.barbearia.Barbearia.service.ServicoService;

import jakarta.validation.Valid;

@Controller
public class ServicoController {
    
    @Autowired
    private ServicoService servicoService;

    @GetMapping("/servicos/listar")
    public String listarServicos(Model model) {
        List<Servico> servicos = servicoService.getAllServicos();
        model.addAttribute("servicos", servicos);
        model.addAttribute("servico", new Servico());
        return "HTML/servicos";
    }

    @PostMapping("/servicos/criar")
    public String criarServico(@Valid @ModelAttribute Servico servico, 
                              BindingResult result, 
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("servicos", servicoService.getAllServicos());
            return "HTML/servicos";
        }
        servicoService.createServico(servico);
        return "redirect:/servicos?success=true";
    }

    @PostMapping("/servico/save")
    public String saveServicoFromModal(@RequestParam String nomeServico, @RequestParam Double valor) {
        servicoService.createServicoFromParams(nomeServico, valor);
        return "redirect:/agendamento?servicoCriado=true";
    }

    @PostMapping("/servicos/editar/{id}")
    public String atualizarServico(@PathVariable Long id,
                                  @Valid @ModelAttribute Servico servico,
                                  BindingResult result,
                                  Model model) {
        if (result.hasErrors()) {
            model.addAttribute("servicos", servicoService.getAllServicos());
            return "HTML/servicos";
        }
        servicoService.updateServico(id, servico);
        return "redirect:/servicos?updated=true";
    }

    @PostMapping("/servico/update")
    public String updateServicoFromModal(@RequestParam Long id, 
                                        @RequestParam String nomeServico, 
                                        @RequestParam Double valor) {
        servicoService.updateServicoFromParams(id, nomeServico, valor);
        return "redirect:/agendamento?servicoAtualizado=true";
    }

    @GetMapping("/servicos/deletar/{id}")
    public String deletarServico(@PathVariable Long id) {
        servicoService.deleteServico(id);
        return "redirect:/servicos?deleted=true";
    }

    @GetMapping("/servico/delete")
    public String deleteServicoFromModal(@RequestParam Long id) {
        servicoService.deleteServico(id);
        return "redirect:/agendamento?servicoExcluido=true";
    }

    @GetMapping("/servicos/{id}")
    public String buscarServico(@PathVariable Long id, Model model) {
        Servico servico = servicoService.getServicoById(id);
        model.addAttribute("servico", servico);
        model.addAttribute("servicos", servicoService.getAllServicos());
        return "HTML/servicos";
    }
}