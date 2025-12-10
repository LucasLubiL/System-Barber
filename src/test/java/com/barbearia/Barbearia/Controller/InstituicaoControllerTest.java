package com.barbearia.Barbearia.Controller;

import com.barbearia.Barbearia.Model.Instituicao;
import com.barbearia.Barbearia.Model.User;
import com.barbearia.Barbearia.service.InstituicaoService;
import com.barbearia.Barbearia.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes unitários para InstituicaoController
 * Testa operações CRUD de instituições e controle de acesso
 */
@WebMvcTest(InstituicaoController.class)
@AutoConfigureMockMvc(addFilters = false)
class InstituicaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InstituicaoService instituicaoService;

    @MockBean
    private UserService userService;

    private User testUser;
    private Instituicao testInstituicao;

    @BeforeEach
    void setUp() {
        testUser = new User();
        ReflectionTestUtils.setField(testUser, "id", 1);
        ReflectionTestUtils.setField(testUser, "email", "teste@email.com");
        ReflectionTestUtils.setField(testUser, "name", "João Silva");
        ReflectionTestUtils.setField(testUser, "password", "senha123");
        ReflectionTestUtils.setField(testUser, "roles", Arrays.asList("ROLE_ADMIN"));

        testInstituicao = new Instituicao();
        ReflectionTestUtils.setField(testInstituicao, "id", 1L);
        ReflectionTestUtils.setField(testInstituicao, "nomeInstituicao", "Barbearia Central");
        ReflectionTestUtils.setField(testInstituicao, "endereco", "Rua das Flores, 123");
        ReflectionTestUtils.setField(testInstituicao, "telefone", "11988887777");
    }

    @Test
    void testGetInstituicaoUnauthenticated() throws Exception {
        mockMvc.perform(get("/instituicao"))
                .andExpect(status().isOk())
                .andExpect(view().name("HTML/instituicao"))
                .andExpect(model().attributeExists("instituicao"))
                .andExpect(model().attribute("isAuthenticated", false))
                .andExpect(model().attribute("userName", ""));
    }

    @Test
    @WithMockUser(username = "teste@email.com")
    void testGetInstituicaoAuthenticated() throws Exception {
        when(userService.findByEmail("teste@email.com")).thenReturn(testUser);

        mockMvc.perform(get("/instituicao"))
                .andExpect(status().isOk())
                .andExpect(view().name("HTML/instituicao"))
                .andExpect(model().attributeExists("instituicao"))
                .andExpect(model().attribute("isAuthenticated", true))
                .andExpect(model().attribute("userName", "João"));

        verify(userService, times(1)).findByEmail("teste@email.com");
    }

    @Test
    @WithMockUser(username = "teste@email.com")
    void testGetInstituicaoWithSuccessParameter() throws Exception {
        when(userService.findByEmail("teste@email.com")).thenReturn(testUser);

        mockMvc.perform(get("/instituicao")
                        .param("success", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("HTML/instituicao"))
                .andExpect(model().attributeExists("showSuccessModal"))
                .andExpect(model().attribute("showSuccessModal", true));
    }

    @Test
    @WithMockUser(username = "teste@email.com")
    void testGetInstituicoesInscritas() throws Exception {
        List<Instituicao> instituicoes = Arrays.asList(testInstituicao);
        when(instituicaoService.getAllInstituicoes()).thenReturn(instituicoes);
        when(userService.findByEmail("teste@email.com")).thenReturn(testUser);

        mockMvc.perform(get("/instituicao/inscritas"))
                .andExpect(status().isOk())
                .andExpect(view().name("HTML/barberAgendamento :: #barber-listOngs"))
                .andExpect(model().attributeExists("instituicoes"))
                .andExpect(model().attributeExists("userName"))
                .andExpect(model().attribute("isAuthenticated", true));

        verify(instituicaoService, times(1)).getAllInstituicoes();
        verify(userService, times(1)).findByEmail("teste@email.com");
    }

    @Test
    void testGetInstituicoesInscritasUnauthenticated() throws Exception {
        List<Instituicao> instituicoes = Arrays.asList(testInstituicao);
        when(instituicaoService.getAllInstituicoes()).thenReturn(instituicoes);

        mockMvc.perform(get("/instituicao/inscritas"))
                .andExpect(status().isOk())
                .andExpect(view().name("HTML/barberAgendamento :: #barber-listOngs"))
                .andExpect(model().attributeExists("instituicoes"))
                .andExpect(model().attributeDoesNotExist("userName"))
                .andExpect(model().attributeDoesNotExist("isAuthenticated"));

        verify(instituicaoService, times(1)).getAllInstituicoes();
    }

    @Test
    @WithMockUser(username = "teste@email.com")
    void testSaveInstituicaoAuthenticated() throws Exception {
        doNothing().when(instituicaoService).saveInstituicao(any(Instituicao.class));

        mockMvc.perform(post("/instituicao/save")
                        .with(csrf())
                        .param("nome", "Barbearia Central")
                        .param("endereco", "Rua das Flores, 123")
                        .param("telefone", "11988887777"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/instituicao?success=true"));

        verify(instituicaoService, times(1)).saveInstituicao(any(Instituicao.class));
    }

    @Test
    void testSaveInstituicaoUnauthenticated() throws Exception {
        doNothing().when(instituicaoService).saveInstituicao(any(Instituicao.class));

        mockMvc.perform(post("/instituicao/save")
                        .with(csrf())
                        .param("nome", "Barbearia Central")
                        .param("endereco", "Rua das Flores, 123")
                        .param("telefone", "11988887777"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/instituicao?success=true"));

        verify(instituicaoService, times(1)).saveInstituicao(any(Instituicao.class));
    }

    @Test
    @WithMockUser(username = "teste@email.com")
    void testSaveInstituicaoWithCompleteData() throws Exception {
        Instituicao novaInstituicao = new Instituicao();
        ReflectionTestUtils.setField(novaInstituicao, "nomeInstituicao", "Nova Barbearia");
        ReflectionTestUtils.setField(novaInstituicao, "endereco", "Av. Principal, 456");
        ReflectionTestUtils.setField(novaInstituicao, "telefone", "11977776666");

        doNothing().when(instituicaoService).saveInstituicao(any(Instituicao.class));

        mockMvc.perform(post("/instituicao/save")
                        .with(csrf())
                        .param("nome", "Nova Barbearia")
                        .param("endereco", "Av. Principal, 456")
                        .param("telefone", "11977776666"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/instituicao?success=true"));

        verify(instituicaoService, times(1)).saveInstituicao(any(Instituicao.class));
    }

    @Test
    @WithMockUser(username = "teste@email.com")
    void testGetInstituicaoWithoutSuccessParameter() throws Exception {
        when(userService.findByEmail("teste@email.com")).thenReturn(testUser);

        mockMvc.perform(get("/instituicao"))
                .andExpect(status().isOk())
                .andExpect(view().name("HTML/instituicao"))
                .andExpect(model().attributeDoesNotExist("showSuccessModal"));
    }

    @Test
    @WithMockUser(username = "teste@email.com")
    void testSaveInstituicaoExtractsUsernameFromAuthentication() throws Exception {
        doNothing().when(instituicaoService).saveInstituicao(any(Instituicao.class));

        mockMvc.perform(post("/instituicao/save")
                        .with(csrf())
                        .param("nome", "Barbearia Teste")
                        .param("endereco", "Rua Teste")
                        .param("telefone", "11999999999"))
                .andExpect(status().is3xxRedirection());

        verify(instituicaoService, times(1)).saveInstituicao(any(Instituicao.class));
    }
}
