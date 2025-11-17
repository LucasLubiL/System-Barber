package com.barbearia.Barbearia.service;

import java.util.List;

import com.barbearia.Barbearia.Model.Instituicao;

public interface InstituicaoService {

    List <Instituicao> getAllInstituicoes();
    void saveInstituicao(Instituicao instituicao);
    Instituicao getInstituicaoById(long id);
    void deleteInstituicaoById(long id);
    
}
