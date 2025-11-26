package com.barbearia.Barbearia.config;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.barbearia.Barbearia.Model.Barbeiro;
import com.barbearia.Barbearia.Repository.BarbeiroRepository;
import com.barbearia.Barbearia.service.BarbeiroService;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private BarbeiroService barbeiroService;

    @Autowired
    private BarbeiroRepository barbeiroRepo;

    @Override
    public void run(String... args) throws Exception {
        if (barbeiroRepo.findByEmail("admin@barbearia.com").isEmpty()) {
            
            Barbeiro admin = new Barbeiro();
            admin.setNomeCompleto("Admin Barbeiro");
            admin.setEmail("admin@barbearia.com");
            admin.setSenha("123456");
            admin.setTelefone("(00) 00000-0000");
            admin.setEspecialidade("Administrador");
            admin.setBiografia("Barbeiro administrador do sistema"); // AGORA É STRING!
            
            barbeiroService.registerNewBarbeiro(admin);
            
            System.out.println("✅ Barbeiro ADMIN criado!");
            System.out.println("   Email: admin@barbearia.com");
            System.out.println("   Senha: 123456");
        }
    }
}