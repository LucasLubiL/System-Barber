package com.barbearia.Barbearia.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.barbearia.Barbearia.Model.Agendamento;

public interface AgendamentoService {

    Agendamento criarAgendamento(Long barbeiroId, String emailCliente, String servicoNome,
            String data, String horario, Double valor,
            Boolean doador, MultipartFile fotoCabelo);

    List<Agendamento> listarAgendamentosPorCliente(Long clienteId);

    Agendamento buscarPorId(Long id);

    void cancelarAgendamento(Long id);

}
