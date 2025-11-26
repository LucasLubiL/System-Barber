package com.barbearia.Barbearia.service;

import java.io.IOException;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import com.barbearia.Barbearia.Model.RegisterUser;
import com.barbearia.Barbearia.Model.User;

public interface UserService {
    public Integer saveUser(User user);

    public void saveRegisterUser(RegisterUser registerUser);

    public boolean validatePasswords(String senha, String confirmarSenha);

    public void registerNewUser(RegisterUser registerUser);

    public User findByEmail(String email);

    public RegisterUser findRegisterUserByEmail(String email);

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    void updateUserProfile(Long id, String nomeCompleto, String dataNascimento, String cpf, String telefone,
            String email, MultipartFile foto) throws IOException;

}