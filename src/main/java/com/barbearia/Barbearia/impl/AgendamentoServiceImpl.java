package com.barbearia.Barbearia.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.barbearia.Barbearia.Model.Agendamento;
import com.barbearia.Barbearia.Model.Barbeiro;
import com.barbearia.Barbearia.Model.RegisterUser;
import com.barbearia.Barbearia.Repository.AgendamentoRepository;
import com.barbearia.Barbearia.Repository.RegisterUserRepository;
import com.barbearia.Barbearia.service.AgendamentoService;
import com.barbearia.Barbearia.service.BarbeiroService;

@Service
public class AgendamentoServiceImpl implements AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private BarbeiroService barbeiroService;

    @Autowired
    private RegisterUserRepository registerUserRepository;

    private static final String UPLOAD_DIR = "uploads/";

    @Override
    public Agendamento criarAgendamento(Long barbeiroId, String emailCliente, String servicoNome,
            String data, String horario, Double valor,
            Boolean doador, MultipartFile fotoCabelo) {
        try {
            // Buscar barbeiro
            Barbeiro barbeiro = barbeiroService.getBarbeiroById(barbeiroId);

            // Buscar cliente pelo email
            Optional<RegisterUser> cliente = registerUserRepository.findByEmail(emailCliente);
            if (cliente.isEmpty()) {
                throw new RuntimeException("Cliente não encontrado");
            }

            // Criar agendamento
            Agendamento agendamento = new Agendamento();
            agendamento.setBarbeiro(barbeiro);
            agendamento.setCliente(cliente.get());
            agendamento.setServico(servicoNome);
            agendamento.setData(LocalDate.parse(data));
            agendamento.setHora(LocalTime.parse(horario));
            agendamento.setValor(valor);
            agendamento.setDoador(doador);
            agendamento.setStatus("Confirmado");

            // Se for doador e enviou foto, salvar como arquivo
            if (doador && fotoCabelo != null && !fotoCabelo.isEmpty()) {
                // Criar diretório se não existir
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Gerar nome único para o arquivo
                String originalFilename = fotoCabelo.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename.replace(extension, "")
                        + extension;

                // Salvar arquivo no sistema
                Path filePath = uploadPath.resolve(uniqueFilename);
                Files.copy(fotoCabelo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Salvar caminho relativo no banco
                agendamento.setFoto(UPLOAD_DIR + uniqueFilename);
            }

            return agendamentoRepository.save(agendamento);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar foto", e);
        }
    }

    @Override
    public List<Agendamento> listarAgendamentosPorCliente(Long clienteId) {
        return agendamentoRepository.findByClienteId(clienteId);
    }

    @Override
    public Agendamento buscarPorId(Long id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
    }

    @Override
    public void cancelarAgendamento(Long id) {
        Agendamento agendamento = buscarPorId(id);
        agendamento.setStatus("Cancelado");
        agendamentoRepository.save(agendamento);
    }

}