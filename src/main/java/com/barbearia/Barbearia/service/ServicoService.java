package com.barbearia.Barbearia.service;

import java.util.List;

import com.barbearia.Barbearia.Model.Servico;

public interface ServicoService {
    
    public List<Servico> getAllServicos();
    public Servico getServicoById(long id);
    public Servico createServico(Servico servico);
    public Servico updateServico(long id, Servico servico);
    public void deleteServico(long id);
    public void createServicoFromParams(String nome, Double valor);
    public void updateServicoFromParams(Long id, String nome, Double valor);


}
