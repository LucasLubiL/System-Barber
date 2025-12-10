package com.barbearia.Barbearia.Controller;

import com.barbearia.Barbearia.Model.Agendamento;
import com.barbearia.Barbearia.Model.Barbeiro;
import com.barbearia.Barbearia.Model.Instituicao;
import com.barbearia.Barbearia.Model.RegisterUser;
import com.barbearia.Barbearia.Model.Servico;
import com.barbearia.Barbearia.Model.User;
import com.barbearia.Barbearia.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private BarbeiroService barbeiroService;

    @MockBean
    private InstituicaoService instituicaoService;

    @MockBean
    private ServicoService servicoService;

    @MockBean
    private AgendamentoService agendamentoService;

    @MockBean
    private FeedbackService feedbackService;

    private User testUser;
    private RegisterUser testRegisterUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        ReflectionTestUtils.setField(testUser, "id", 1);
        ReflectionTestUtils.setField(testUser, "email", "teste@email.com");
        ReflectionTestUtils.setField(testUser, "name", "João Silva");
        ReflectionTestUtils.setField(testUser, "password", "senha123");
        ReflectionTestUtils.setField(testUser, "roles", Arrays.asList("ROLE_USER"));

        testRegisterUser = new RegisterUser();
        testRegisterUser.setId(1L);
        testRegisterUser.setEmail("cliente@email.com");
        testRegisterUser.setNomeCompleto("Maria Silva");
        testRegisterUser.setSenha("senha123");
        testRegisterUser.setTelefone("11999999999");
        testRegisterUser.setCpf("123.456.789-00");
        testRegisterUser.setDataNascimento(java.time.LocalDate.of(1990, 1, 1));
    }

    @Test
    void testRootRedirect() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    void testGetIndexUnauthenticated() throws Exception {
        when(feedbackService.listarFeedbacks()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("HTML/index"))
                .andExpect(model().attributeExists("feedbacks"))
                .andExpect(model().attribute("isAuthenticated", false))
                .andExpect(model().attributeDoesNotExist("userName"));

        verify(feedbackService, times(1)).listarFeedbacks();
    }

    @Test
    @WithMockUser(username = "teste@email.com")
    void testGetIndexAuthenticated() throws Exception {
        when(userService.findByEmail("teste@email.com")).thenReturn(testUser);
        when(feedbackService.listarFeedbacks()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("HTML/index"))
                .andExpect(model().attributeExists("feedbacks"))
                .andExpect(model().attribute("isAuthenticated", true))
                .andExpect(model().attribute("userName", "João"));

        verify(userService, times(1)).findByEmail("teste@email.com");
        verify(feedbackService, times(1)).listarFeedbacks();
    }

    @Test
    void testGetLoginWithoutErrors() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("HTML/login"))
                .andExpect(model().attributeDoesNotExist("errorMessage"))
                .andExpect(model().attributeDoesNotExist("successMessage"));
    }

    @Test
    void testGetLoginWithError() throws Exception {
        mockMvc.perform(get("/login").param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("HTML/login"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Email ou senha inválidos!"));
    }

    @Test
    void testGetLoginWithRegisteredSuccess() throws Exception {
        mockMvc.perform(get("/login").param("registered", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("HTML/login"))
                .andExpect(model().attributeExists("successMessage"))
                .andExpect(model().attribute("successMessage", "Cadastro realizado com sucesso! Faça login."));
    }

    @Test
    void testGetRegisterPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("HTML/register"))
                .andExpect(model().attributeExists("registerUser"));
    }

    @Test
    void testGetRegisterWithError() throws Exception {
        mockMvc.perform(get("/register").param("error", "senha"))
                .andExpect(status().isOk())
                .andExpect(view().name("HTML/register"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "As senhas não coincidem!"));
    }

    @Test
    void testPostRegisterSuccess() throws Exception {
        when(userService.validatePasswords("senha123", "senha123")).thenReturn(true);
        doNothing().when(userService).registerNewUser(any(RegisterUser.class));

        mockMvc.perform(post("/saveUser")
                        .with(csrf())
                        .param("nomeCompleto", "João Silva")
                        .param("email", "joao@email.com")
                        .param("telefone", "11999999999")
                        .param("cpf", "123.456.789-00")
                        .param("dataNascimento", "1990-01-01")
                        .param("senha", "senha123")
                        .param("confirmarSenha", "senha123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        verify(userService, times(1)).registerNewUser(any(RegisterUser.class));
    }

    @Test
    void testPostRegisterEmailAlreadyExists() throws Exception {
        when(userService.validatePasswords("senha123", "senha123")).thenReturn(true);
        doThrow(new RuntimeException("Email já cadastrado"))
                .when(userService).registerNewUser(any(RegisterUser.class));

        org.junit.jupiter.api.Assertions.assertThrows(jakarta.servlet.ServletException.class, () -> {
            mockMvc.perform(post("/saveUser")
                    .with(csrf())
                    .param("nomeCompleto", "João Silva")
                    .param("email", "joao@email.com")
                    .param("telefone", "11999999999")
                    .param("cpf", "123.456.789-00")
                    .param("dataNascimento", "1990-01-01")
                    .param("senha", "senha123")
                    .param("confirmarSenha", "senha123"));
        });

        verify(userService, times(1)).registerNewUser(any(RegisterUser.class));
    }

    @Test
    void testPostRegisterPasswordMismatch() throws Exception {
        when(userService.validatePasswords("senha123", "senha456")).thenReturn(false);

        mockMvc.perform(post("/saveUser")
                        .with(csrf())
                        .param("nomeCompleto", "João Silva")
                        .param("email", "joao@email.com")
                        .param("telefone", "11999999999")
                        .param("cpf", "123.456.789-00")
                        .param("dataNascimento", "1990-01-01")
                        .param("senha", "senha123")
                        .param("confirmarSenha", "senha456"))
                .andExpect(status().isOk())
                .andExpect(view().name("HTML/register"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "As senhas não coincidem!"));

        verify(userService, never()).registerNewUser(any(RegisterUser.class));
    }

    @Test
    @WithMockUser(username = "barbeiro@email.com", authorities = "ROLE_BARBER")
    void testRedirectAgendamentoForBarber() throws Exception {
        Barbeiro barbeiro = new Barbeiro();
        barbeiro.setId(1L);
        barbeiro.setEmail("barbeiro@email.com");
        barbeiro.setNomeCompleto("Carlos Barbeiro");
        barbeiro.setTelefone("11988887777");
        barbeiro.setEspecialidade("Corte e Barba");
        barbeiro.setBiografia("Profissional experiente");
        barbeiro.setSenha("senha123");

        List<Instituicao> instituicoes = new ArrayList<>();
        List<Agendamento> agendamentos = new ArrayList<>();
        List<Barbeiro> barbeiros = new ArrayList<>();
        List<Servico> servicos = new ArrayList<>();

        when(barbeiroService.findBarbeiroByEmail("barbeiro@email.com")).thenReturn(barbeiro);
        when(instituicaoService.getAllInstituicoes()).thenReturn(instituicoes);
        when(barbeiroService.getAllBarbeiros()).thenReturn(barbeiros);
        when(servicoService.getAllServicos()).thenReturn(servicos);
        when(agendamentoService.listarAgendamentosPorBarbeiroEData(anyLong(), any())).thenReturn(agendamentos);
        when(agendamentoService.calcularReceitaPrevista(any())).thenReturn(0.0);

        mockMvc.perform(get("/agendamento"))
                .andExpect(status().isOk())
                .andExpect(view().name("HTML/barberAgendamento"))
                .andExpect(model().attributeExists("barbeiro"))
                .andExpect(model().attributeExists("agendamentos"))
                .andExpect(model().attributeExists("instituicoes"));

        verify(barbeiroService, times(1)).findBarbeiroByEmail("barbeiro@email.com");
    }

    @Test
    @WithMockUser(username = "cliente@email.com")
    void testRedirectAgendamentoForClient() throws Exception {
        when(userService.findRegisterUserByEmail("cliente@email.com")).thenReturn(testRegisterUser);
        when(barbeiroService.getAllBarbeiros()).thenReturn(new ArrayList<>());
        when(servicoService.getAllServicos()).thenReturn(new ArrayList<>());
        when(agendamentoService.listarAgendamentosPorEmail("cliente@email.com")).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/agendamento"))
                .andExpect(status().isOk())
                .andExpect(view().name("HTML/clienteAgendamento"))
                .andExpect(model().attributeExists("registerUser"))
                .andExpect(model().attributeExists("barbeiros"))
                .andExpect(model().attributeExists("servicos"))
                .andExpect(model().attributeExists("agendamentos"));

        verify(userService, times(1)).findRegisterUserByEmail("cliente@email.com");
        verify(barbeiroService, times(1)).getAllBarbeiros();
    }

    @Test
    @WithMockUser(username = "barbeiro@email.com", authorities = "ROLE_BARBER")
    void testUpdateInstituicaoSuccess() throws Exception {
        Barbeiro barbeiro = new Barbeiro();
        barbeiro.setId(1L);
        barbeiro.setEmail("barbeiro@email.com");
        barbeiro.setNomeCompleto("Barbeiro Teste");
        barbeiro.setTelefone("11988887777");
        barbeiro.setEspecialidade("Corte e Barba");
        barbeiro.setBiografia("Profissional da área");
        barbeiro.setSenha("senha123");

        when(barbeiroService.findBarbeiroByEmail("barbeiro@email.com")).thenReturn(barbeiro);
        when(instituicaoService.getAllInstituicoes()).thenReturn(new ArrayList<>());
        when(barbeiroService.getAllBarbeiros()).thenReturn(new ArrayList<>());
        when(servicoService.getAllServicos()).thenReturn(new ArrayList<>());
        when(agendamentoService.listarAgendamentosPorBarbeiroEData(eq(1L), any(LocalDate.class)))
                .thenReturn(new ArrayList<>());
        when(agendamentoService.calcularReceitaPrevista(any())).thenReturn(0.0);

        mockMvc.perform(get("/agendamento"))
                .andExpect(status().isOk())
                .andExpect(view().name("HTML/barberAgendamento"))
                .andExpect(model().attributeExists("barbeiro"));

        verify(barbeiroService, times(1)).findBarbeiroByEmail("barbeiro@email.com");
    }
}
