package com.barbearia.Barbearia.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.barbearia.Barbearia.Model.Agendamento;

public interface AgendamentoService {

    Agendamento criarAgendamento(Long barbeiroId, String emailCliente, String servicoNome,
            String data, String horario, Double valor,
            Boolean doador, MultipartFile fotoCabelo);

    List<Agendamento> listarAgendamentosPorCliente(Long clienteId);

    List<Agendamento> listarAgendamentosPorEmail(String email);

    Agendamento buscarPorId(Long id);

    void cancelarAgendamento(Long id);

    void concluirAgendamento(Long id);

    List<Agendamento> listarAgendamentosPorBarbeiro(Long barbeiroId);
    
    List<Agendamento> listarAgendamentosPorBarbeiroEData(Long barbeiroId, LocalDate data);

    double calcularReceitaPrevista(List<Agendamento> agendamentos);

}
