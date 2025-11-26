package com.barbearia.Barbearia.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.barbearia.Barbearia.Model.Barbeiro;

public interface BarbeiroService {

    public Barbeiro findBarbeiroByEmail(String email);

    public void updateBarbeiroProfile(Long id, String nomeCompleto, String telefone, String especialidade, String biografia,
            String email, MultipartFile foto) throws IOException;

    public void registerNewBarbeiro(Barbeiro barbeiro);

    public void saveBarbeiro(Barbeiro barbeiro);

    public List<Barbeiro> getAllBarbeiros();

}