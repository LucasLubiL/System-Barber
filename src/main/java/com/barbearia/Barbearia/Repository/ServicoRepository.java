package com.barbearia.Barbearia.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.barbearia.Barbearia.Model.Servico;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long>{

    
}
