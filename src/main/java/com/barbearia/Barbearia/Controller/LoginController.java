package com.barbearia.Barbearia.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    
    @GetMapping("/home")
    public String getIndex() {
        return "html/index";
    }

    @GetMapping("/login")
    public String getLogin() {
        return "html/login";
    }

    @GetMapping("/register")
    public String getRegister() {
        return "html/register";
    }

}
