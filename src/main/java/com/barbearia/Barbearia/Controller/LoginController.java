package com.barbearia.Barbearia.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    
    @GetMapping("/html")
    public String getMethodName() {
        return "html/index";
    }

}
