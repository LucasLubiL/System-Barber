package com.barbearia.Barbearia.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.barbearia.Barbearia.Model.Servico;
import com.barbearia.Barbearia.Repository.ServicoRepository;
import com.barbearia.Barbearia.service.ServicoService;

@Service
public class ServicoServiceImpl implements ServicoService {
    
    @Autowired
    private ServicoRepository servicoRepository;

    @Override
    public List<Servico> getAllServicos() {
        return servicoRepository.findAll();
    }

    @Override
    public Servico getServicoById(long id) {
        return servicoRepository.findById(id).orElse(null);
    }

    @Override
    public Servico createServico(Servico servico) {
        return servicoRepository.save(servico);
    }

    @Override
    public Servico updateServico(long id, Servico servico) {
        servico.setId(id);
        return servicoRepository.save(servico);
    }

    @Override
    public void deleteServico(long id) {
        servicoRepository.deleteById(id);
    }

    @Override
    public void createServicoFromParams(String nomeServico, Double valor) {
        Servico servico = new Servico();
        servico.setNomeServico(nomeServico);
        servico.setValor(valor);
        servicoRepository.save(servico);
    }

    @Override
    public void updateServicoFromParams(Long id, String nomeServico, Double valor) {
        Servico servico = servicoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));
        servico.setNomeServico(nomeServico);
        servico.setValor(valor);
        servicoRepository.save(servico);
    }

}
