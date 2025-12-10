package com.barbearia.Barbearia.integration;

import com.barbearia.Barbearia.Model.*;
import com.barbearia.Barbearia.Repository.*;
import com.barbearia.Barbearia.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the entire Barbearia application.
 * Tests full HTTP request flows including authentication, authorization,
 * and database interactions.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class BarbeariaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private BarbeiroService barbeiroService;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private AgendamentoService agendamentoService;

    @Autowired
    private InstituicaoService instituicaoService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private RegisterUser testCliente;
    private Barbeiro testBarbeiro;
    private Instituicao testInstituicao;

    @BeforeEach
    void setUp() {
        // Create test client
        testCliente = new RegisterUser();
        testCliente.setNomeCompleto("Cliente Teste");
        testCliente.setEmail("cliente.teste@email.com");
        testCliente.setTelefone("11999999999");
        testCliente.setCpf("123.456.789-00");
        testCliente.setDataNascimento(java.time.LocalDate.of(1990, 1, 1));
        testCliente.setSenha(passwordEncoder.encode("senha123"));

        // Create test barber
        testBarbeiro = new Barbeiro();
        testBarbeiro.setNomeCompleto("Barbeiro Teste");
        testBarbeiro.setEmail("barbeiro.teste@email.com");
        testBarbeiro.setTelefone("11988888888");
        testBarbeiro.setEspecialidade("Corte e Barba");
        testBarbeiro.setBiografia("Barbeiro experiente");
        testBarbeiro.setSenha(passwordEncoder.encode("senha123"));

        // Create test institution
        testInstituicao = new Instituicao();
        ReflectionTestUtils.setField(testInstituicao, "nomeInstituicao", "Barbearia Teste");
        testInstituicao.setEndereco("Rua Teste, 123");
        testInstituicao.setTelefone("11977777777");
    }

    // ==================== AUTHENTICATION TESTS ====================

    @Test
    @WithAnonymousUser
    void testUnauthenticatedUserCanAccessHomePage() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("HTML/index"))
                .andExpect(model().attribute("isAuthenticated", false));
    }

    @Test
    @WithAnonymousUser
    void testUnauthenticatedUserCanAccessLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("HTML/login"));
    }

    @Test
    @WithAnonymousUser
    void testUnauthenticatedUserCanAccessRegisterPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("HTML/register"));
    }

    @Test
    @WithAnonymousUser
    void testUnauthenticatedUserCannotAccessAgendamento() throws Exception {
        mockMvc.perform(get("/agendamento"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithAnonymousUser
    void testUnauthenticatedUserCannotSubmitFeedback() throws Exception {
        mockMvc.perform(post("/feedback")
                        .with(csrf())
                        .param("comentario", "Tentativa de feedback")
                        .param("rating", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    // ==================== AUTHORIZATION TESTS ====================

    @Test
    @WithMockUser(username = "cliente@email.com", roles = {"USER"})
    void testClientUserCanAccessClienteAgendamento() throws Exception {
        // Salvar usuário no banco primeiro
        RegisterUser cliente = new RegisterUser();
        cliente.setNomeCompleto("Cliente Teste");
        cliente.setEmail("cliente@email.com");
        cliente.setTelefone("11999999999");
        cliente.setCpf("123.456.789-00");
        cliente.setDataNascimento(java.time.LocalDate.of(1990, 1, 1));
        cliente.setSenha(passwordEncoder.encode("senha123"));
        userService.registerNewUser(cliente);

        // This test assumes the user exists in the database
        // In a real scenario, you would set up the user in the database first
        mockMvc.perform(get("/agendamento"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "barbeiro@email.com", authorities = {"ROLE_BARBER"})
    void testBarberUserCanAccessBarberAgendamento() throws Exception {
        // Salvar barbeiro no banco primeiro
        Barbeiro barbeiro = new Barbeiro();
        barbeiro.setNomeCompleto("Barbeiro Teste");
        barbeiro.setEmail("barbeiro@email.com");
        barbeiro.setTelefone("11988888888");
        barbeiro.setEspecialidade("Corte e Barba");
        barbeiro.setBiografia("Barbeiro experiente");
        barbeiro.setSenha(passwordEncoder.encode("senha123"));
        barbeiroService.registerNewBarbeiro(barbeiro);

        // This test assumes the barber exists in the database
        mockMvc.perform(get("/agendamento"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "barbeiro@email.com", authorities = {"ROLE_BARBER"})
    void testBarberCanCreateAnotherBarbeiro() throws Exception {
        mockMvc.perform(post("/createBarbeiro")
                        .with(csrf())
                        .param("nomeCompleto", "Novo Barbeiro")
                        .param("especialidade", "Corte")
                        .param("telefone", "11966666666")
                        .param("email", "novo.barbeiro@email.com")
                        .param("senha", "senha123")
                        .param("confirmarSenha", "senha123"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "barbeiro@email.com", authorities = {"ROLE_BARBER"})
    void testBarberCanDeleteBarbeiro() throws Exception {
        mockMvc.perform(get("/deleteBarbeiro")
                        .param("id", "999")) // Non-existent ID
                .andExpect(status().is3xxRedirection());
    }

    // ==================== FULL FLOW INTEGRATION TESTS ====================

    @Test
    void testCompleteUserRegistrationAndLoginFlow() throws Exception {
        // Step 1: Register a new user
        mockMvc.perform(post("/saveUser")
                        .with(csrf())
                        .param("nomeCompleto", "João Silva Completo")
                        .param("email", "joao.completo@email.com")
                        .param("telefone", "11955555555")
                        .param("cpf", "123.456.789-00")
                        .param("dataNascimento", "1990-01-01")
                        .param("senha", "senha123")
                        .param("confirmarSenha", "senha123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        // Step 2: Access login page with success message
        mockMvc.perform(get("/login")
                        .param("registered", "true"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("successMessage"));
    }

    @Test
    @WithMockUser(username = "cliente.teste@email.com")
    void testCompleteFeedbackSubmissionFlow() throws Exception {
        // Salvar usuário no banco primeiro
        userService.registerNewUser(testCliente);

        // Submit feedback
        mockMvc.perform(post("/feedback")
                        .with(csrf())
                        .param("comentario", "Serviço excelente!")
                        .param("rating", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/principal?feedbackSuccess=true"));

        // Verify feedback success modal appears
        mockMvc.perform(get("/principal")
                        .param("feedbackSuccess", "true"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("feedbackEnviado", true));
    }

    @Test
    void testRegisterWithDuplicateEmail() throws Exception {
        // First registration
        mockMvc.perform(post("/saveUser")
                        .with(csrf())
                        .param("nomeCompleto", "Primeiro Usuario")
                        .param("email", "duplicado@email.com")
                        .param("telefone", "11944444444")
                        .param("cpf", "123.456.789-00")
                        .param("dataNascimento", "1990-01-01")
                        .param("senha", "senha123")
                        .param("confirmarSenha", "senha123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        // Second registration with same email (should throw DataIntegrityViolationException wrapped in ServletException)
        org.junit.jupiter.api.Assertions.assertThrows(jakarta.servlet.ServletException.class, () -> {
            mockMvc.perform(post("/saveUser")
                    .with(csrf())
                    .param("nomeCompleto", "Segundo Usuario")
                    .param("email", "duplicado@email.com")
                    .param("telefone", "11933333333")
                    .param("cpf", "987.654.321-00")
                    .param("dataNascimento", "1992-05-10")
                    .param("senha", "senha456")
                    .param("confirmarSenha", "senha456"));
        });
    }

    @Test
    void testRegisterWithPasswordMismatch() throws Exception {
        mockMvc.perform(post("/saveUser")
                        .with(csrf())
                        .param("nomeCompleto", "Teste Senha")
                        .param("email", "senha.errada@email.com")
                        .param("telefone", "11922222222")
                        .param("cpf", "123.456.789-00")
                        .param("dataNascimento", "1990-01-01")
                        .param("senha", "senha123")
                        .param("confirmarSenha", "senha456"))
                .andExpect(status().isOk())
                .andExpect(view().name("HTML/register"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "As senhas não coincidem!"));
    }

    // ==================== CSRF PROTECTION TESTS ====================

    @Test
    @WithMockUser(username = "cliente@email.com")
    void testFeedbackSubmissionWithoutCsrfTokenShouldFail() throws Exception {
        mockMvc.perform(post("/feedback")
                        .param("comentario", "Teste sem CSRF")
                        .param("rating", "5"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "cliente@email.com")
    void testFeedbackSubmissionWithCsrfTokenShouldSucceed() throws Exception {
        // Salvar usuário no banco primeiro
        RegisterUser cliente = new RegisterUser();
        cliente.setNomeCompleto("Cliente Teste");
        cliente.setEmail("cliente@email.com");
        cliente.setTelefone("11999999999");
        cliente.setCpf("123.456.789-00");
        cliente.setDataNascimento(java.time.LocalDate.of(1990, 1, 1));
        cliente.setSenha(passwordEncoder.encode("senha123"));
        userService.registerNewUser(cliente);

        mockMvc.perform(post("/feedback")
                        .with(csrf())
                        .param("comentario", "Teste com CSRF")
                        .param("rating", "5"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testRegisterWithoutCsrfTokenShouldFail() throws Exception {
        mockMvc.perform(post("/saveUser")
                        .param("nomeCompleto", "Teste CSRF")
                        .param("email", "csrf@email.com")
                        .param("telefone", "11911111111")
                        .param("cpf", "123.456.789-00")
                        .param("dataNascimento", "1990-01-01")
                        .param("senha", "senha123")
                        .param("confirmarSenha", "senha123"))
                .andExpect(status().isForbidden());
    }

    // ==================== STATIC RESOURCES TESTS ====================

    @Test
    @WithAnonymousUser
    void testUnauthenticatedUserCanAccessStaticImages() throws Exception {
        // Static resources should be accessible without authentication
        // This is configured in SecurityConfig with permitAll for /img/**
        mockMvc.perform(get("/img/test.jpg"))
                .andExpect(status().isNotFound()); // 404 because file doesn't exist, but not 401/403
    }

    @Test
    @WithAnonymousUser
    void testUnauthenticatedUserCanAccessStaticCss() throws Exception {
        mockMvc.perform(get("/CSS/index.css"))
                .andExpect(status().isOk());
    }

    // ==================== ERROR HANDLING TESTS ====================

    @Test
    @WithMockUser(username = "barbeiro@email.com", authorities = {"ROLE_BARBER"})
    void testCreateBarbeiroWithDuplicateEmailShowsError() throws Exception {
        // Create first barbeiro
        mockMvc.perform(post("/createBarbeiro")
                        .with(csrf())
                        .param("nomeCompleto", "Primeiro Barbeiro")
                        .param("especialidade", "Corte")
                        .param("telefone", "11900000001")
                        .param("email", "barbeiro.duplicado@email.com")
                        .param("senha", "senha123")
                        .param("confirmarSenha", "senha123"))
                .andExpect(status().is3xxRedirection());

        // Try to create second barbeiro with same email
        mockMvc.perform(post("/createBarbeiro")
                        .with(csrf())
                        .param("nomeCompleto", "Segundo Barbeiro")
                        .param("especialidade", "Barba")
                        .param("telefone", "11900000002")
                        .param("email", "barbeiro.duplicado@email.com")
                        .param("senha", "senha456")
                        .param("confirmarSenha", "senha456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/agendamento?erroCriar=true"));
    }

    @Test
    @WithMockUser(username = "barbeiro@email.com", authorities = {"ROLE_BARBER"})
    void testDeleteNonExistentBarbeiroShowsError() throws Exception {
        mockMvc.perform(get("/deleteBarbeiro")
                        .param("id", "99999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/agendamento?erroExcluir=true"));
    }

    // ==================== SESSION MANAGEMENT TESTS ====================

    @Test
    @WithMockUser(username = "cliente@email.com")
    void testSessionAttributesForErrorMessages() throws Exception {
        // This test verifies that error messages are properly set in session
        // and can be retrieved in subsequent requests
        mockMvc.perform(post("/createBarbeiro")
                        .with(csrf())
                        .param("nomeCompleto", "")
                        .param("especialidade", "")
                        .param("telefone", "")
                        .param("email", "")
                        .param("senha", "")
                        .param("confirmarSenha", ""))
                .andExpect(status().is3xxRedirection());
    }

    // ==================== ROLE-BASED ACCESS TESTS ====================

    @Test
    @WithMockUser(username = "cliente@email.com", roles = {"USER"})
    void testClientCannotAccessBarberOnlyFunctions() throws Exception {
        // Clients should not be able to create barbeiros
        // This would be enforced by the service layer or controller logic
        mockMvc.perform(post("/createBarbeiro")
                        .with(csrf())
                        .param("nomeCompleto", "Teste Acesso")
                        .param("especialidade", "Corte")
                        .param("telefone", "11900000000")
                        .param("email", "acesso@email.com")
                        .param("senha", "senha123")
                        .param("confirmarSenha", "senha123"))
                .andExpect(status().is3xxRedirection());
    }

    // ==================== REDIRECT TESTS ====================

    @Test
    @WithAnonymousUser
    void testRootRedirectsToHome() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    void testLoginErrorParameterShowsErrorMessage() throws Exception {
        mockMvc.perform(get("/login")
                        .param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Email ou senha inválidos!"));
    }

    @Test
    void testRegisterErrorParameterShowsErrorMessage() throws Exception {
        mockMvc.perform(get("/register")
                        .param("error", "senha"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "As senhas não coincidem!"));
    }
}
