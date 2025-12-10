package com.barbearia.Barbearia.Controller;

import com.barbearia.Barbearia.Model.Agendamento;
import com.barbearia.Barbearia.service.AgendamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AgendamentoController.class)
@AutoConfigureMockMvc(addFilters = false)
class AgendamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AgendamentoService agendamentoService;

    private Agendamento testAgendamento;

    @BeforeEach
    void setUp() {
        testAgendamento = new Agendamento();
        testAgendamento.setId(1L);
        testAgendamento.setServico("Corte de Cabelo");
        testAgendamento.setValor(50.0);
    }

    @Test
    @WithMockUser(username = "cliente@email.com")
    void testCriarAgendamentoSuccess() throws Exception {
        MockMultipartFile foto = new MockMultipartFile(
                "fotoCabelo",
                "cabelo.jpg",
                "image/jpeg",
                "imagem teste".getBytes()
        );

        when(agendamentoService.criarAgendamento(
                anyLong(), anyString(), anyString(), anyString(),
                anyString(), anyDouble(), anyBoolean(), any()
        )).thenReturn(testAgendamento);

        mockMvc.perform(multipart("/saveAgendamento")
                        .file(foto)
                        .with(csrf())
                        .param("barbeiroId", "1")
                        .param("servicoId", "1")
                        .param("servicoNome", "Corte de Cabelo")
                        .param("data", "2024-01-15")
                        .param("horario", "14:00")
                        .param("valor", "50.0")
                        .param("doador", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/agendamento?success=true"));

        verify(agendamentoService, times(1)).criarAgendamento(
                eq(1L), eq("cliente@email.com"), eq("Corte de Cabelo"),
                eq("2024-01-15"), eq("14:00"), eq(50.0), eq(false), any()
        );
    }

    @Test
    @WithMockUser(username = "cliente@email.com")
    void testCriarAgendamentoWithoutPhoto() throws Exception {
        when(agendamentoService.criarAgendamento(
                anyLong(), anyString(), anyString(), anyString(),
                anyString(), anyDouble(), anyBoolean(), isNull()
        )).thenReturn(testAgendamento);

        mockMvc.perform(multipart("/saveAgendamento")
                        .with(csrf())
                        .param("barbeiroId", "1")
                        .param("servicoNome", "Corte de Cabelo")
                        .param("data", "2024-01-15")
                        .param("horario", "14:00")
                        .param("valor", "50.0")
                        .param("doador", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/agendamento?success=true"));

        verify(agendamentoService, times(1)).criarAgendamento(
                eq(1L), eq("cliente@email.com"), eq("Corte de Cabelo"),
                eq("2024-01-15"), eq("14:00"), eq(50.0), eq(false), isNull()
        );
    }

    @Test
    @WithMockUser(username = "cliente@email.com")
    void testCriarAgendamentoDoador() throws Exception {
        MockMultipartFile foto = new MockMultipartFile(
                "fotoCabelo",
                "cabelo.jpg",
                "image/jpeg",
                "imagem teste".getBytes()
        );

        when(agendamentoService.criarAgendamento(
                anyLong(), anyString(), anyString(), anyString(),
                anyString(), anyDouble(), anyBoolean(), any()
        )).thenReturn(testAgendamento);

        mockMvc.perform(multipart("/saveAgendamento")
                        .file(foto)
                        .with(csrf())
                        .param("barbeiroId", "1")
                        .param("servicoNome", "Doação de Cabelo")
                        .param("data", "2024-01-15")
                        .param("horario", "10:00")
                        .param("valor", "0.0")
                        .param("doador", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/agendamento?success=true"));

        verify(agendamentoService, times(1)).criarAgendamento(
                eq(1L), eq("cliente@email.com"), eq("Doação de Cabelo"),
                eq("2024-01-15"), eq("10:00"), eq(0.0), eq(true), any()
        );
    }

    @Test
    @WithMockUser(username = "cliente@email.com")
    void testCriarAgendamentoHorarioIndisponivel() throws Exception {
        when(agendamentoService.criarAgendamento(
                anyLong(), anyString(), anyString(), anyString(),
                anyString(), anyDouble(), anyBoolean(), any()
        )).thenThrow(new RuntimeException("Horário indisponível para este barbeiro"));

        org.junit.jupiter.api.Assertions.assertThrows(jakarta.servlet.ServletException.class, () -> {
            mockMvc.perform(multipart("/saveAgendamento")
                    .with(csrf())
                    .param("barbeiroId", "1")
                    .param("servicoNome", "Corte de Cabelo")
                    .param("data", "2024-01-15")
                    .param("horario", "14:00")
                    .param("valor", "50.0")
                    .param("doador", "false"));
        });

        verify(agendamentoService, times(1)).criarAgendamento(
                anyLong(), anyString(), anyString(), anyString(),
                anyString(), anyDouble(), anyBoolean(), any()
        );
    }

    @Test
    @WithMockUser(username = "cliente@email.com")
    void testCriarAgendamentoGenericError() throws Exception {
        when(agendamentoService.criarAgendamento(
                anyLong(), anyString(), anyString(), anyString(),
                anyString(), anyDouble(), anyBoolean(), any()
        )).thenThrow(new RuntimeException("Erro ao criar agendamento"));

        org.junit.jupiter.api.Assertions.assertThrows(jakarta.servlet.ServletException.class, () -> {
            mockMvc.perform(multipart("/saveAgendamento")
                    .with(csrf())
                    .param("barbeiroId", "1")
                    .param("servicoNome", "Corte de Cabelo")
                    .param("data", "2024-01-15")
                    .param("horario", "14:00")
                    .param("valor", "50.0")
                    .param("doador", "false"));
        });

        verify(agendamentoService, times(1)).criarAgendamento(
                anyLong(), anyString(), anyString(), anyString(),
                anyString(), anyDouble(), anyBoolean(), any()
        );
    }

    @Test
    @WithMockUser(username = "cliente@email.com", authorities = "ROLE_CLIENT")
    void testCancelarAgendamentoSuccess() throws Exception {
        doNothing().when(agendamentoService).cancelarAgendamento(1L);

        mockMvc.perform(post("/cancelarAgendamento")
                        .with(csrf())
                        .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/agendamento?cancelled=true"));

        verify(agendamentoService, times(1)).cancelarAgendamento(1L);
    }

    @Test
    @WithMockUser(username = "barbeiro@email.com", authorities = "ROLE_BARBER")
    void testConcluirAgendamentoSuccess() throws Exception {
        doNothing().when(agendamentoService).concluirAgendamento(1L);

        mockMvc.perform(get("/agendamento/concluir")
                        .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/agendamento?concluded=true"));

        verify(agendamentoService, times(1)).concluirAgendamento(1L);
    }

    @Test
    @WithMockUser(username = "cliente@email.com")
    void testCriarAgendamentoWithServicoId() throws Exception {
        when(agendamentoService.criarAgendamento(
                anyLong(), anyString(), anyString(), anyString(),
                anyString(), anyDouble(), anyBoolean(), any()
        )).thenReturn(testAgendamento);

        mockMvc.perform(multipart("/saveAgendamento")
                        .with(csrf())
                        .param("barbeiroId", "1")
                        .param("servicoId", "2")
                        .param("servicoNome", "Barba")
                        .param("data", "2024-01-20")
                        .param("horario", "16:00")
                        .param("valor", "30.0")
                        .param("doador", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/agendamento?success=true"));
    }

    @Test
    @WithMockUser(username = "cliente@email.com")
    void testCriarAgendamentoWithoutServicoId() throws Exception {
        when(agendamentoService.criarAgendamento(
                anyLong(), anyString(), anyString(), anyString(),
                anyString(), anyDouble(), anyBoolean(), any()
        )).thenReturn(testAgendamento);

        mockMvc.perform(multipart("/saveAgendamento")
                        .with(csrf())
                        .param("barbeiroId", "1")
                        .param("servicoNome", "Serviço Customizado")
                        .param("data", "2024-01-20")
                        .param("horario", "16:00")
                        .param("valor", "70.0")
                        .param("doador", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/agendamento?success=true"));
    }

    @Test
    @WithMockUser
    void testTesteAgendamentoEndpoint() throws Exception {
        mockMvc.perform(get("/testeAgendamento"))
                .andExpect(status().isOk())
                .andExpect(content().string("Controller funcionando!"));
    }

    @Test
    @WithMockUser(username = "cliente@email.com")
    void testCriarAgendamentoExtractsUserEmailFromAuthentication() throws Exception {
        when(agendamentoService.criarAgendamento(
                anyLong(), eq("cliente@email.com"), anyString(), anyString(),
                anyString(), anyDouble(), anyBoolean(), any()
        )).thenReturn(testAgendamento);

        mockMvc.perform(multipart("/saveAgendamento")
                        .with(csrf())
                        .param("barbeiroId", "1")
                        .param("servicoNome", "Corte")
                        .param("data", "2024-01-15")
                        .param("horario", "10:00")
                        .param("valor", "40.0")
                        .param("doador", "false"))
                .andExpect(status().is3xxRedirection());

        // Verify that the correct email from authentication was used
        verify(agendamentoService, times(1)).criarAgendamento(
                anyLong(), eq("cliente@email.com"), anyString(), anyString(),
                anyString(), anyDouble(), anyBoolean(), any()
        );
    }
}
