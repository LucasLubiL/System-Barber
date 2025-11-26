package com.barbearia.Barbearia.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.barbearia.Barbearia.Model.Barbeiro;
import com.barbearia.Barbearia.Model.User;
import com.barbearia.Barbearia.Repository.BarbeiroRepository;
import com.barbearia.Barbearia.Repository.UserRepository;
import com.barbearia.Barbearia.service.BarbeiroService;

import jakarta.transaction.Transactional;

@Service
public class BarbeiroServiceImpl implements BarbeiroService {

    @Autowired
    private BarbeiroRepository barbeiroRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public Barbeiro findBarbeiroByEmail(String email) {
        return barbeiroRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Barbeiro não encontrado com email: " + email));
    }

    @Override
    @Transactional
    public void updateBarbeiroProfile(Long id, String nomeCompleto, String telefone, String especialidade,
            String biografia, String email, MultipartFile foto) throws IOException {
        Barbeiro barbeiro = barbeiroRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Barbeiro não encontrado"));

        String emailAntigo = barbeiro.getEmail();

        barbeiro.setNomeCompleto(nomeCompleto);
        barbeiro.setTelefone(telefone);
        barbeiro.setEspecialidade(especialidade);
        barbeiro.setBiografia(biografia); // REMOVIDA A CONVERSÃO PARA DATA!
        barbeiro.setEmail(email);

        // SALVA A FOTO NO SERVIDOR (se foi enviada)
        if (foto != null && !foto.isEmpty()) {
            String nomeArquivo = UUID.randomUUID().toString() + "_" + foto.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/fotos/");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(nomeArquivo);
            Files.copy(foto.getInputStream(), filePath);

            String fotoCaminho = "/uploads/fotos/" + nomeArquivo;
            barbeiro.setFoto(fotoCaminho);
        }

        barbeiroRepo.save(barbeiro);

        User user = userRepo.findByEmail(emailAntigo)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado na tabela de autenticação"));

        user.setName(nomeCompleto);
        user.setEmail(email);
        userRepo.save(user);
    }

    @Override
    public void saveBarbeiro(Barbeiro barbeiro) {
        String encodedPasswd = passwordEncoder.encode(barbeiro.getSenha());
        barbeiro.setSenha(encodedPasswd);
        barbeiroRepo.save(barbeiro);
    }

    @Override
    @Transactional
    public void registerNewBarbeiro(Barbeiro barbeiro) {
        String senhaOriginal = barbeiro.getSenha();

        // SALVA O BARBEIRO
        saveBarbeiro(barbeiro);

        // CRIA O USER COM ROLE_BARBER
        User user = new User();
        user.setEmail(barbeiro.getEmail());
        user.setPassword(senhaOriginal);
        user.setName(barbeiro.getNomeCompleto());
        user.setRoles(Arrays.asList("ROLE_BARBER"));

        String encodedPasswd = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPasswd);
        userRepo.save(user);
    }

    @Override
    public List<Barbeiro> getAllBarbeiros() {
        return barbeiroRepo.findAll();
    }

}