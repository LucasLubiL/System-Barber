package com.barbearia.Barbearia.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.barbearia.Barbearia.Model.Barbeiro;

public interface BarbeiroService {

    public Barbeiro findBarbeiroByEmail(String email);

    public void updateBarbeiroProfile(Long id, String nomeCompleto, String telefone, String especialidade,
            String biografia,
            String email, MultipartFile foto) throws IOException;

    public void registerNewBarbeiro(Barbeiro barbeiro);

    public void saveBarbeiro(Barbeiro barbeiro);

    public List<Barbeiro> getAllBarbeiros();

    public Barbeiro getBarbeiroById(Long id);
    
    public void deleteBarbeiro(Long id);

    void createBarbeiroFromParams(String nomeCompleto, String especialidade, String telefone,
            String email, String senha, String confirmarSenha);

}