package com.barbearia.Barbearia.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.barbearia.Barbearia.Model.Instituicao;
import com.barbearia.Barbearia.service.InstituicaoService;

public class InstituicaoServiceImpl implements InstituicaoService {

    @Autowired
    private Instituicao instituicaoRepository;

    @Override
    public void saveInstituicao(Instituicao instituicao) {
        this.instituicaoRepository.save(instituicao);
    }

    @Override
    public List<Instituicao> getAllInstituicoes() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllInstituicoes'");
    }

    @Override
    public Instituicao getInstituicaoById(long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getInstituicaoById'");
    }

    @Override
    public void deleteInstituicaoById(long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteInstituicaoById'");
    }
}