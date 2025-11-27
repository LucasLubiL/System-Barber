package com.barbearia.Barbearia.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.barbearia.Barbearia.Model.RegisterUser;
import com.barbearia.Barbearia.Model.User;
import com.barbearia.Barbearia.Repository.RegisterUserRepository;
import com.barbearia.Barbearia.Repository.UserRepository;
import com.barbearia.Barbearia.service.UserService;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RegisterUserRepository registerUserRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private static final String UPLOAD_DIR = "uploads/";

    @Override
    public Integer saveUser(User user) {
        String passwd = user.getPassword();
        String encodedPasswd = passwordEncoder.encode(passwd);
        user.setPassword(encodedPasswd);
        user = userRepo.save(user);
        return user.getId();
    }

    @Override
    public void saveRegisterUser(RegisterUser registerUser) {
        String encodedPasswd = passwordEncoder.encode(registerUser.getSenha());
        registerUser.setSenha(encodedPasswd);
        registerUserRepo.save(registerUser);
    }

    @Override
    public boolean validatePasswords(String senha, String confirmarSenha) {
        return senha != null && senha.equals(confirmarSenha);
    }

    @Override
    public User findByEmail(String email) {
        Optional<User> opt = userRepo.findByEmail(email);
        return opt.orElse(null);
    }

    @Override
    public void registerNewUser(RegisterUser registerUser) {
        String senhaOriginal = registerUser.getSenha();

        saveRegisterUser(registerUser);

        User user = new User();
        user.setEmail(registerUser.getEmail());
        user.setPassword(senhaOriginal);
        user.setName(registerUser.getNomeCompleto());
        user.setRoles(Arrays.asList("ROLE_CLIENT"));

        saveUser(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> opt = userRepo.findByEmail(email);

        if (opt.isEmpty())
            throw new UsernameNotFoundException("User with email: " + email + " not found!");
        else {
            User user = opt.get();
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    getAuthorities(user.getRoles()));
        }
    }

    @Override
    @Transactional
    public void updateUserProfile(Long id, String nomeCompleto, String dataNascimento, String cpf, String telefone,
            String email, MultipartFile foto) throws IOException {
        RegisterUser registerUser = registerUserRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String emailAntigo = registerUser.getEmail();

        registerUser.setNomeCompleto(nomeCompleto);
        registerUser.setDataNascimento(LocalDate.parse(dataNascimento, DateTimeFormatter.ISO_LOCAL_DATE));
        registerUser.setCpf(cpf);
        registerUser.setTelefone(telefone);
        registerUser.setEmail(email);

        // SALVAR FOTO COMO ARQUIVO (igual antes)
        if (foto != null && !foto.isEmpty()) {
            // Criar diretório se não existir
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Gerar nome único para o arquivo
            String originalFilename = foto.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename.replace(extension, "")
                    + extension;

            // Salvar arquivo no sistema
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(foto.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Salvar caminho relativo no banco
            registerUser.setFoto(UPLOAD_DIR + uniqueFilename);
        }

        registerUserRepo.save(registerUser);

        // ATUALIZA A TABELA DE AUTENTICAÇÃO
        User user = userRepo.findByEmail(emailAntigo)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado na tabela de autenticação"));

        user.setName(nomeCompleto);
        user.setEmail(email);
        userRepo.save(user);
    }

    @Override
    public RegisterUser findRegisterUserByEmail(String email) {
        return registerUserRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com email: " + email));
    }

    private Set<GrantedAuthority> getAuthorities(List<String> roles) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }
}