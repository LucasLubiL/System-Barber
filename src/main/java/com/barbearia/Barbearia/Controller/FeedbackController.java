package com.barbearia.Barbearia.Controller;

import com.barbearia.Barbearia.Model.Feedback;
import com.barbearia.Barbearia.Repository.FeedbackRepository;
import com.barbearia.Barbearia.service.FeedbackService;
import com.barbearia.Barbearia.service.UserService;
import com.barbearia.Barbearia.Model.RegisterUser;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private UserService userService;

    @GetMapping("/principal")
    public String getPrincipal(Model model, @AuthenticationPrincipal UserDetails userDetails, 
                               @RequestParam(required = false) Boolean feedbackSuccess) {
        if (userDetails != null) {
            RegisterUser user = userService.findRegisterUserByEmail(userDetails.getUsername());
            String primeiroNome = user.getNomeCompleto().split(" ")[0];
            model.addAttribute("userName", primeiroNome);
            model.addAttribute("isAuthenticated", true);
        } else {
            model.addAttribute("isAuthenticated", false);
        }
        model.addAttribute("feedbacks", feedbackService.listarFeedbacks());
        model.addAttribute("feedbackEnviado", feedbackSuccess != null && feedbackSuccess);
        return "HTML/index";
    }

    @PostMapping("/feedback")
    public String salvarFeedback(
            @RequestParam("comentario") String comentario,
            @RequestParam("rating") Integer rating,
            @AuthenticationPrincipal UserDetails usuario,
            Model model) {
        if (usuario == null) {
            return "redirect:/login";
        }

        RegisterUser registerUser = userService.findRegisterUserByEmail(usuario.getUsername());

        Feedback feedback = new Feedback();
        feedback.setComentario(comentario);
        feedback.setAvaliacao(rating);
        feedback.setCliente(registerUser); // agora é RegisterUser
        feedback.setData(LocalDate.now());
        feedbackService.salvarFeedback(feedback);

        return "redirect:/principal?feedbackSuccess=true";

    }
}