package com.barbearia.Barbearia.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.barbearia.Barbearia.service.AgendamentoService;

@Controller
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @PostMapping("/saveAgendamento")
    public String criarAgendamento(
            @RequestParam("barbeiroId") Long barbeiroId,
            @RequestParam(value = "servicoId", required = false) Long servicoId,
            @RequestParam("servicoNome") String servicoNome,
            @RequestParam("data") String data,
            @RequestParam("horario") String horario,
            @RequestParam("valor") Double valor,
            @RequestParam(value = "doador", defaultValue = "false") Boolean doador,
            @RequestParam(value = "fotoCabelo", required = false) MultipartFile fotoCabelo,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            // Pegar email do usuário logado (igual UserController)
            String emailCliente = userDetails.getUsername();

            System.out.println("=== CRIANDO AGENDAMENTO ===");
            System.out.println("Email Cliente: " + emailCliente);
            System.out.println("Barbeiro ID: " + barbeiroId);
            System.out.println("Serviço: " + servicoNome);
            System.out.println("Data: " + data);
            System.out.println("Horário: " + horario);
            System.out.println("Valor: " + valor);
            System.out.println("Doador: " + doador);

            agendamentoService.criarAgendamento(barbeiroId, emailCliente, servicoNome,
                    data, horario, valor, doador, fotoCabelo);

            return "redirect:/agendamento?success=true";

        } catch (Exception e) {
            System.err.println("ERRO ao criar agendamento: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/agendamento?error=true";
        }
    }

    @PostMapping("/cancelarAgendamento")
    public String cancelarAgendamento(@RequestParam Long id) {
        agendamentoService.cancelarAgendamento(id);
        return "redirect:/agendamento?cancelled=true";
    }

    // ✅ TESTE: Adicione isso temporariamente
    @GetMapping("/testeAgendamento")
    @ResponseBody
    public String teste() {
        return "Controller funcionando!";
    }
}