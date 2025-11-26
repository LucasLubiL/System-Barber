package com.barbearia.Barbearia.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.barbearia.Barbearia.Model.Instituicao;
import com.barbearia.Barbearia.Repository.InstituicaoRepository;
import com.barbearia.Barbearia.service.InstituicaoService;

@Service
public class InstituicaoServiceImpl implements InstituicaoService {

    @Autowired
    private InstituicaoRepository instituicaoRepository;

    @Override
    public void saveInstituicao(Instituicao instituicao) {
        this.instituicaoRepository.save(instituicao);
    }

    @Override
    public List<Instituicao> getAllInstituicoes() {
        return instituicaoRepository.findAll();
    }

    @Override
    public Instituicao getInstituicaoById(long id) {
        return instituicaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instituição não encontrada"));
    }

    @Override
    public void deleteInstituicaoById(long id) {
        instituicaoRepository.deleteById(id);
    }
}