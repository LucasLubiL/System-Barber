package com.barbearia.Barbearia.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.barbearia.Barbearia.Model.Agendamento;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    List<Agendamento> findByClienteId(Long clienteId);

    List<Agendamento> findByBarbeiroId(Long barbeiroId);
    
    List<Agendamento> findByBarbeiroIdAndData(Long barbeiroId, LocalDate data);

}
