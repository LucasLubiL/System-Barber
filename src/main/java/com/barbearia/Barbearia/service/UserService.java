package com.barbearia.Barbearia.service;

import com.barbearia.Barbearia.Model.RegisterUser;
import com.barbearia.Barbearia.Model.User;

public interface UserService {
    public Integer saveUser(User user);
    public void saveRegisterUser(RegisterUser registerUser);
    public boolean validatePasswords(String senha, String confirmarSenha);
    public void registerNewUser(RegisterUser registerUser);
    public User findByEmail(String email);
}