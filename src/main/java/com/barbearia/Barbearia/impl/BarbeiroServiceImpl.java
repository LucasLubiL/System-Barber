package com.barbearia.Barbearia.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
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
        barbeiro.setBiografia(biografia);
        barbeiro.setEmail(email);

        if (foto != null && !foto.isEmpty()) {
            String base64Image = "data:image/jpeg;base64," +
                    Base64.getEncoder().encodeToString(foto.getBytes());
            barbeiro.setFoto(base64Image);
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

        saveBarbeiro(barbeiro);

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

    @Override
    public Barbeiro getBarbeiroById(Long id) {
        return barbeiroRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Barbeiro não encontrado"));
    }

    @Override
    @Transactional
    public void createBarbeiroFromParams(String nomeCompleto, String especialidade, String telefone,
            String email, String senha, String confirmarSenha) {
        System.out.println("=== CRIANDO BARBEIRO ===");
        System.out.println("Email: " + email);
        System.out.println("Nome: " + nomeCompleto);

        // Validação: email já existe?
        if (barbeiroRepo.existsByEmail(email)) {
            throw new RuntimeException("E-mail já cadastrado!");
        }

        // Validação: senhas conferem?
        if (!senha.equals(confirmarSenha)) {
            throw new RuntimeException("As senhas não conferem!");
        }

        // Cria o objeto Barbeiro
        Barbeiro barbeiro = new Barbeiro();
        barbeiro.setNomeCompleto(nomeCompleto);
        barbeiro.setEspecialidade(especialidade);
        barbeiro.setTelefone(telefone);
        barbeiro.setEmail(email);
        barbeiro.setSenha(senha);

        // Criptografa e salva o barbeiro
        String encodedPassword = passwordEncoder.encode(senha);
        barbeiro.setSenha(encodedPassword);
        barbeiroRepo.save(barbeiro);

        System.out.println("Barbeiro salvo! ID: " + barbeiro.getId());

        // Cria o User com ROLE_BARBER
        User user = new User();
        user.setEmail(email);
        user.setName(nomeCompleto);
        user.setPassword(passwordEncoder.encode(senha));
        user.setRoles(Arrays.asList("ROLE_BARBER"));
        userRepo.save(user);

        System.out.println("User criado! ID: " + user.getId());
    }

    @Override
    @Transactional
    public void deleteBarbeiro(Long id) {
        Barbeiro barbeiro = barbeiroRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Barbeiro não encontrado"));

        // Deleta o usuário da tabela User também
        User user = userRepo.findByEmail(barbeiro.getEmail())
                .orElse(null);

        if (user != null) {
            userRepo.delete(user);
        }

        barbeiroRepo.deleteById(id);
    }
    
}