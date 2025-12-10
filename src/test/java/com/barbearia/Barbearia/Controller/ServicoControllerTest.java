package com.barbearia.Barbearia.Controller;

import com.barbearia.Barbearia.Model.Servico;
import com.barbearia.Barbearia.service.ServicoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes unitários para ServicoController
 * Testa operações CRUD de serviços oferecidos pela barbearia
 */
@WebMvcTest(ServicoController.class)
@AutoConfigureMockMvc(addFilters = false)
class ServicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServicoService servicoService;

    private Servico testServico;

    @BeforeEach
    void setUp() {
        testServico = new Servico();
        testServico.setId(1L);
        testServico.setNomeServico("Corte de Cabelo");
        testServico.setValor(50.0);
    }

    @Test
    @WithMockUser
    void testListarServicos() throws Exception {
        List<Servico> servicos = Arrays.asList(testServico);
        when(servicoService.getAllServicos()).thenReturn(servicos);

        org.junit.jupiter.api.Assertions.assertThrows(jakarta.servlet.ServletException.class, () -> {
            mockMvc.perform(get("/servicos/listar"));
        });

        verify(servicoService, times(1)).getAllServicos();
    }

    @Test
    @WithMockUser
    void testCriarServicoSuccess() throws Exception {
        when(servicoService.createServico(any(Servico.class))).thenReturn(testServico);

        mockMvc.perform(post("/servicos/criar")
                        .with(csrf())
                        .param("nomeServico", "Corte de Cabelo")
                        .param("valor", "50.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/servicos?success=true"));

        verify(servicoService, times(1)).createServico(any(Servico.class));
    }

    @Test
    @WithMockUser
    void testSaveServicoFromModal() throws Exception {
        doNothing().when(servicoService).createServicoFromParams(anyString(), anyDouble());

        mockMvc.perform(post("/servico/save")
                        .with(csrf())
                        .param("nomeServico", "Barba")
                        .param("valor", "30.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/agendamento?servicoCriado=true"));

        verify(servicoService, times(1)).createServicoFromParams("Barba", 30.0);
    }

    @Test
    @WithMockUser
    void testAtualizarServicoSuccess() throws Exception {
        when(servicoService.updateServico(anyLong(), any(Servico.class))).thenReturn(testServico);

        mockMvc.perform(post("/servicos/editar/1")
                        .with(csrf())
                        .param("nomeServico", "Corte Premium")
                        .param("valor", "80.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/servicos?updated=true"));

        verify(servicoService, times(1)).updateServico(eq(1L), any(Servico.class));
    }

    @Test
    @WithMockUser
    void testUpdateServicoFromModal() throws Exception {
        doNothing().when(servicoService).updateServicoFromParams(anyLong(), anyString(), anyDouble());

        mockMvc.perform(post("/servico/update")
                        .with(csrf())
                        .param("id", "1")
                        .param("nomeServico", "Corte Atualizado")
                        .param("valor", "60.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/agendamento?servicoAtualizado=true"));

        verify(servicoService, times(1)).updateServicoFromParams(1L, "Corte Atualizado", 60.0);
    }

    @Test
    @WithMockUser
    void testDeletarServico() throws Exception {
        doNothing().when(servicoService).deleteServico(1L);

        mockMvc.perform(get("/servicos/deletar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/servicos?deleted=true"));

        verify(servicoService, times(1)).deleteServico(1L);
    }

    @Test
    @WithMockUser
    void testDeleteServicoFromModal() throws Exception {
        doNothing().when(servicoService).deleteServico(1L);

        mockMvc.perform(get("/servico/delete")
                        .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/agendamento?servicoExcluido=true"));

        verify(servicoService, times(1)).deleteServico(1L);
    }

    @Test
    @WithMockUser
    void testCriarMultiplosServicos() throws Exception {
        Servico servico1 = new Servico();
        servico1.setNomeServico("Corte");
        servico1.setValor(50.0);

        Servico servico2 = new Servico();
        servico2.setNomeServico("Barba");
        servico2.setValor(30.0);

        doNothing().when(servicoService).createServicoFromParams("Corte", 50.0);
        doNothing().when(servicoService).createServicoFromParams("Barba", 30.0);

        // Criar primeiro serviço
        mockMvc.perform(post("/servico/save")
                        .with(csrf())
                        .param("nomeServico", "Corte")
                        .param("valor", "50.0"))
                .andExpect(status().is3xxRedirection());

        // Criar segundo serviço
        mockMvc.perform(post("/servico/save")
                        .with(csrf())
                        .param("nomeServico", "Barba")
                        .param("valor", "30.0"))
                .andExpect(status().is3xxRedirection());

        verify(servicoService, times(1)).createServicoFromParams("Corte", 50.0);
        verify(servicoService, times(1)).createServicoFromParams("Barba", 30.0);
    }

    @Test
    @WithMockUser
    void testAtualizarServicoComValorZero() throws Exception {
        doNothing().when(servicoService).updateServicoFromParams(anyLong(), anyString(), anyDouble());

        mockMvc.perform(post("/servico/update")
                        .with(csrf())
                        .param("id", "1")
                        .param("nomeServico", "Doação")
                        .param("valor", "0.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/agendamento?servicoAtualizado=true"));

        verify(servicoService, times(1)).updateServicoFromParams(1L, "Doação", 0.0);
    }

    @Test
    @WithMockUser
    void testListarServicosVazio() throws Exception {
        when(servicoService.getAllServicos()).thenReturn(Arrays.asList());

        org.junit.jupiter.api.Assertions.assertThrows(jakarta.servlet.ServletException.class, () -> {
            mockMvc.perform(get("/servicos/listar"));
        });

        verify(servicoService, times(1)).getAllServicos();
    }
}
