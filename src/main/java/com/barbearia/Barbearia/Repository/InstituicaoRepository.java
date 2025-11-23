package com.barbearia.Barbearia.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.barbearia.Barbearia.Model.Instituicao;

@Repository
public interface InstituicaoRepository extends JpaRepository<Instituicao, Long>{
    
}
