package com.barbearia.Barbearia.Controller;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import java.util.Base64;
import java.util.List;
import java.io.IOException;

import com.barbearia.Barbearia.Model.Agendamento;
import com.barbearia.Barbearia.Model.Barbeiro;
import com.barbearia.Barbearia.Model.RegisterUser;
import com.barbearia.Barbearia.Model.User;
import com.barbearia.Barbearia.service.AgendamentoService;
import com.barbearia.Barbearia.service.BarbeiroService;
import com.barbearia.Barbearia.service.InstituicaoService;
import com.barbearia.Barbearia.service.ServicoService;
import com.barbearia.Barbearia.service.UserService;

import jakarta.validation.Valid;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private BarbeiroService barbeiroService;

    @Autowired
    private InstituicaoService instituicaoService;

    @Autowired
    private ServicoService servicoService;

    @Autowired
    private AgendamentoService agendamentoService;

    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String getIndex(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            User user = userService.findByEmail(userDetails.getUsername());
            String primeiroNome = user.getName().split(" ")[0];
            model.addAttribute("userName", primeiroNome);
            model.addAttribute("isAuthenticated", true);
        } else {
            model.addAttribute("isAuthenticated", false);
        }
        return "HTML/index";
    }

    @GetMapping("/login")
    public String getLogin(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "registered", required = false) String registered,
            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Email ou senha inválidos!");
        }
        if (registered != null) {
            model.addAttribute("successMessage", "Cadastro realizado com sucesso! Faça login.");
        }
        return "HTML/login";
    }

    @GetMapping("/register")
    public String getRegister(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "As senhas não coincidem!");
        }
        model.addAttribute("registerUser", new RegisterUser());
        return "HTML/register";
    }

    @PostMapping("/saveUser")
    public String saveUser(@Valid @ModelAttribute RegisterUser registerUser,
            BindingResult result,
            @RequestParam("confirmarSenha") String confirmarSenha,
            Model model) {

        if (result.hasErrors()) {
            return "HTML/register";
        }

        if (!userService.validatePasswords(registerUser.getSenha(), confirmarSenha)) {
            model.addAttribute("errorMessage", "As senhas não coincidem!");
            return "HTML/register";
        }

        userService.registerNewUser(registerUser);

        return "redirect:/login?registered";
    }

    @GetMapping("/registerBarbeiro")
    public String getRegisterBarbeiro(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "As senhas não coincidem!");
        }
        model.addAttribute("barbeiro", new Barbeiro());
        return "HTML/registerBarbeiro";
    }

    @PostMapping("/saveBarbeiro")
    public String saveBarbeiro(@Valid @ModelAttribute Barbeiro barbeiro,
            BindingResult result,
            @RequestParam("confirmarSenha") String confirmarSenha,
            Model model) {

        if (result.hasErrors()) {
            return "HTML/registerBarbeiro";
        }

        if (!userService.validatePasswords(barbeiro.getSenha(), confirmarSenha)) {
            model.addAttribute("errorMessage", "As senhas não coincidem!");
            return "HTML/registerBarbeiro";
        }

        barbeiroService.registerNewBarbeiro(barbeiro);

        return "redirect:/login?registered";
    }

    /*
     * @GetMapping("/agendamento")
     * public String redirectAgendamento(@AuthenticationPrincipal UserDetails
     * userDetails,
     * 
     * @RequestParam(required = false) String success,
     * Model model) {
     * 
     * if (userDetails == null) {
     * return "redirect:/login";
     * }
     * 
     * // BUSCA O RegisterUser
     * RegisterUser registerUser =
     * userService.findRegisterUserByEmail(userDetails.getUsername());
     * model.addAttribute("registerUser", registerUser);
     * 
     * // ADICIONA AS VARIÁVEIS PARA O HEADER (igual ao /home)
     * String primeiroNome = registerUser.getNomeCompleto().split(" ")[0];
     * model.addAttribute("userName", primeiroNome);
     * model.addAttribute("isAuthenticated", true);
     * 
     * // VERIFICA SE É BARBEIRO OU CLIENTE
     * boolean isBarber = userDetails.getAuthorities().stream()
     * .anyMatch(auth -> auth.getAuthority().equals("ROLE_BARBER"));
     * 
     * if (isBarber) {
     * return "HTML/barberAgendamento";
     * } else {
     * return "HTML/clienteAgendamento";
     * }
     * }
     */

    @GetMapping("/agendamento")
    public String redirectAgendamento(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String success,
            @RequestParam(required = false) String data,
            Model model) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        // VERIFICA SE É BARBEIRO OU CLIENTE
        boolean isBarber = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_BARBER"));

        if (isBarber) {
            // BUSCA O BARBEIRO
            Barbeiro barbeiro = barbeiroService.findBarbeiroByEmail(userDetails.getUsername());
            model.addAttribute("barbeiro", barbeiro);

            model.addAttribute("instituicoes", instituicaoService.getAllInstituicoes());
            model.addAttribute("barbeiros", barbeiroService.getAllBarbeiros());
            model.addAttribute("servicos", servicoService.getAllServicos());

            LocalDate dataSelecionada = (data != null) ? LocalDate.parse(data) : LocalDate.now();
            List<Agendamento> agendamentos = agendamentoService.listarAgendamentosPorBarbeiroEData(
                    barbeiro.getId(),
                    dataSelecionada);
            double receitaPrevista = agendamentoService.calcularReceitaPrevista(agendamentos);

            model.addAttribute("agendamentos", agendamentos);
            model.addAttribute("dataSelecionada", dataSelecionada);
            model.addAttribute("receitaPrevista", receitaPrevista);

            String primeiroNome = barbeiro.getNomeCompleto().split(" ")[0];
            model.addAttribute("userName", primeiroNome);
            model.addAttribute("isAuthenticated", true);

            return "HTML/barberAgendamento";
        } else {
            // BUSCA O CLIENTE
            RegisterUser registerUser = userService.findRegisterUserByEmail(userDetails.getUsername());
            model.addAttribute("registerUser", registerUser);

            // CARREGA DADOS PARA OS MODAIS DO CLIENTE TAMBÉM
            model.addAttribute("barbeiros", barbeiroService.getAllBarbeiros());
            model.addAttribute("servicos", servicoService.getAllServicos());

            // ✅ BUSCAR AGENDAMENTOS DO CLIENTE
            List<Agendamento> agendamentos = agendamentoService.listarAgendamentosPorEmail(userDetails.getUsername());
            model.addAttribute("agendamentos", agendamentos);

            String primeiroNome = registerUser.getNomeCompleto().split(" ")[0];
            model.addAttribute("userName", primeiroNome);
            model.addAttribute("isAuthenticated", true);

            return "HTML/clienteAgendamento";
        }
    }

    @PostMapping("/putUser")
    public String updateUser(@RequestParam Long id,
            @RequestParam String nomeCompleto,
            @RequestParam String dataNascimento,
            @RequestParam String cpf,
            @RequestParam String telefone,
            @RequestParam String email,
            @RequestParam(required = false) MultipartFile foto,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        String emailAntigo = userDetails.getUsername();

        // DELEGA TUDO PARA O SERVICE
        userService.updateUserProfile(id, nomeCompleto, dataNascimento, cpf, telefone, email, foto);

        // SE O EMAIL MUDOU, ATUALIZA A SESSÃO
        if (!emailAntigo.equals(email)) {
            UserDetails updatedUserDetails = userDetailsService.loadUserByUsername(email);
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    updatedUserDetails,
                    updatedUserDetails.getPassword(),
                    updatedUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }

        return "redirect:/agendamento?success=true";
    }

    @PostMapping("/putBarbeiro")
    public String updateBarbeiro(@RequestParam Long id,
            @RequestParam String nomeCompleto,
            @RequestParam String telefone,
            @RequestParam String especialidade,
            @RequestParam(required = false) String biografia,
            @RequestParam String email,
            @RequestParam(required = false) MultipartFile foto,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        String emailAntigo = userDetails.getUsername();

        barbeiroService.updateBarbeiroProfile(id, nomeCompleto, telefone, especialidade, biografia, email, foto);

        if (!emailAntigo.equals(email)) {
            UserDetails updatedUserDetails = userDetailsService.loadUserByUsername(email);
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    updatedUserDetails,
                    updatedUserDetails.getPassword(),
                    updatedUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }

        return "redirect:/agendamento?success=true";
    }

}