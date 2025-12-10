package com.barbearia.Barbearia.Controller;

import com.barbearia.Barbearia.Model.Barbeiro;
import com.barbearia.Barbearia.service.BarbeiroService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BarbeiroController.class)
@AutoConfigureMockMvc(addFilters = false)
class BarbeiroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BarbeiroService barbeiroService;

    private Barbeiro testBarbeiro;
    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        testBarbeiro = new Barbeiro();
        testBarbeiro.setId(1L);
        testBarbeiro.setNomeCompleto("Carlos Silva");
        testBarbeiro.setEmail("carlos@barbearia.com");
        testBarbeiro.setEspecialidade("Corte e Barba");
        testBarbeiro.setTelefone("11988887777");
        testBarbeiro.setBiografia("Barbeiro experiente");
        testBarbeiro.setSenha("senha123");

        session = new MockHttpSession();
    }

    @Test
    @WithMockUser(authorities = "ROLE_BARBER")
    void testCriarBarbeiroSuccess() throws Exception {
        doNothing().when(barbeiroService).createBarbeiroFromParams(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
        );

        mockMvc.perform(post("/createBarbeiro")
                        .with(csrf())
                        .param("nomeCompleto", "Carlos Silva")
                        .param("especialidade", "Corte e Barba")
                        .param("telefone", "11988887777")
                        .param("email", "carlos@barbearia.com")
                        .param("senha", "senha123")
                        .param("confirmarSenha", "senha123")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/agendamento?barbeiroCriado=true"));

        verify(barbeiroService, times(1)).createBarbeiroFromParams(
                "Carlos Silva", "Corte e Barba", "11988887777",
                "carlos@barbearia.com", "senha123", "senha123"
        );
    }

    @Test
    @WithMockUser(authorities = "ROLE_BARBER")
    void testCriarBarbeiroEmailDuplicado() throws Exception {
        doThrow(new RuntimeException("Email já cadastrado"))
                .when(barbeiroService).createBarbeiroFromParams(
                        anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
                );

        mockMvc.perform(post("/createBarbeiro")
                        .with(csrf())
                        .param("nomeCompleto", "Carlos Silva")
                        .param("especialidade", "Corte e Barba")
                        .param("telefone", "11988887777")
                        .param("email", "carlos@barbearia.com")
                        .param("senha", "senha123")
                        .param("confirmarSenha", "senha123")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/agendamento?erroCriar=true"));

        // Verify that error message was set in session
        assert session.getAttribute("erroCriarBarbeiro").equals("Email já cadastrado");
    }

    @Test
    @WithMockUser(authorities = "ROLE_BARBER")
    void testCriarBarbeiroPasswordMismatch() throws Exception {
        doThrow(new RuntimeException("As senhas não coincidem"))
                .when(barbeiroService).createBarbeiroFromParams(
                        anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
                );

        mockMvc.perform(post("/createBarbeiro")
                        .with(csrf())
                        .param("nomeCompleto", "Carlos Silva")
                        .param("especialidade", "Corte e Barba")
                        .param("telefone", "11988887777")
                        .param("email", "carlos@barbearia.com")
                        .param("senha", "senha123")
                        .param("confirmarSenha", "senha456")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/agendamento?erroCriar=true"));

        assert session.getAttribute("erroCriarBarbeiro").equals("As senhas não coincidem");
    }

    @Test
    @WithMockUser(authorities = "ROLE_BARBER")
    void testCriarBarbeiroWithEmptyFields() throws Exception {
        doThrow(new RuntimeException("Todos os campos são obrigatórios"))
                .when(barbeiroService).createBarbeiroFromParams(
                        anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
                );

        mockMvc.perform(post("/createBarbeiro")
                        .with(csrf())
                        .param("nomeCompleto", "")
                        .param("especialidade", "")
                        .param("telefone", "")
                        .param("email", "")
                        .param("senha", "")
                        .param("confirmarSenha", "")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/agendamento?erroCriar=true"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_BARBER")
    void testExcluirBarbeiroSuccess() throws Exception {
        doNothing().when(barbeiroService).deleteBarbeiro(1L);

        mockMvc.perform(get("/deleteBarbeiro")
                        .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/agendamento?barbeiroExcluido=true"));

        verify(barbeiroService, times(1)).deleteBarbeiro(1L);
    }

    @Test
    @WithMockUser(authorities = "ROLE_BARBER")
    void testExcluirBarbeiroNotFound() throws Exception {
        doThrow(new RuntimeException("Barbeiro não encontrado"))
                .when(barbeiroService).deleteBarbeiro(999L);

        mockMvc.perform(get("/deleteBarbeiro")
                        .param("id", "999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/agendamento?erroExcluir=true"));

        verify(barbeiroService, times(1)).deleteBarbeiro(999L);
    }

    @Test
    @WithMockUser(authorities = "ROLE_BARBER")
    void testExcluirBarbeiroWithAgendamentos() throws Exception {
        doThrow(new RuntimeException("Barbeiro possui agendamentos ativos"))
                .when(barbeiroService).deleteBarbeiro(1L);

        mockMvc.perform(get("/deleteBarbeiro")
                        .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/agendamento?erroExcluir=true"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_BARBER")
    void testCriarBarbeiroSetsSessionErrorMessage() throws Exception {
        String errorMessage = "Email já existe no sistema";
        doThrow(new RuntimeException(errorMessage))
                .when(barbeiroService).createBarbeiroFromParams(
                        anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
                );

        mockMvc.perform(post("/createBarbeiro")
                        .with(csrf())
                        .param("nomeCompleto", "João")
                        .param("especialidade", "Corte")
                        .param("telefone", "11999999999")
                        .param("email", "joao@email.com")
                        .param("senha", "123")
                        .param("confirmarSenha", "123")
                        .session(session))
                .andExpect(status().is3xxRedirection());

        // Verify exact error message is stored in session
        assert session.getAttribute("erroCriarBarbeiro").equals(errorMessage);
    }

    @Test
    @WithMockUser(authorities = "ROLE_BARBER")
    void testCriarBarbeiroWithValidData() throws Exception {
        Barbeiro novoBarbeiro = new Barbeiro();
        novoBarbeiro.setId(2L);
        novoBarbeiro.setNomeCompleto("Pedro Santos");
        novoBarbeiro.setEmail("pedro@barbearia.com");
        novoBarbeiro.setTelefone("11977777777");
        novoBarbeiro.setEspecialidade("Barba");
        novoBarbeiro.setBiografia("Especialista em barbas");
        novoBarbeiro.setSenha("senha456");

        doNothing().when(barbeiroService).createBarbeiroFromParams(
                "Pedro Santos", "Barba", "11977777777",
                "pedro@barbearia.com", "senha456", "senha456"
        );

        mockMvc.perform(post("/createBarbeiro")
                        .with(csrf())
                        .param("nomeCompleto", "Pedro Santos")
                        .param("especialidade", "Barba")
                        .param("telefone", "11977777777")
                        .param("email", "pedro@barbearia.com")
                        .param("senha", "senha456")
                        .param("confirmarSenha", "senha456")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/agendamento?barbeiroCriado=true"));

        verify(barbeiroService, times(1)).createBarbeiroFromParams(
                "Pedro Santos", "Barba", "11977777777",
                "pedro@barbearia.com", "senha456", "senha456"
        );
    }
}
