package com.barbearia.Barbearia.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InstituicaoController {
    
    @GetMapping("/instituicao")
    public String getInstituicao() {
        return "html/instituicao";
    }

}
