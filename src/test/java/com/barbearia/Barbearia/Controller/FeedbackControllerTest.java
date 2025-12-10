package com.barbearia.Barbearia.Controller;

import com.barbearia.Barbearia.Model.Feedback;
import com.barbearia.Barbearia.Model.RegisterUser;
import com.barbearia.Barbearia.service.FeedbackService;
import com.barbearia.Barbearia.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FeedbackController.class)
@AutoConfigureMockMvc(addFilters = false)
class FeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeedbackService feedbackService;

    @MockBean
    private UserService userService;

    private RegisterUser testRegisterUser;
    private Feedback testFeedback;

    @BeforeEach
    void setUp() {
        testRegisterUser = new RegisterUser();
        testRegisterUser.setId(1L);
        testRegisterUser.setEmail("cliente@email.com");
        testRegisterUser.setNomeCompleto("Maria Silva");
        testRegisterUser.setSenha("senha123");
        testRegisterUser.setTelefone("11999999999");
        testRegisterUser.setCpf("123.456.789-00");
        testRegisterUser.setDataNascimento(java.time.LocalDate.of(1990, 1, 1));

        testFeedback = new Feedback();
        testFeedback.setId(1L);
        testFeedback.setComentario("Excelente serviço!");
        testFeedback.setAvaliacao(5);
        testFeedback.setCliente(testRegisterUser);
        testFeedback.setData(LocalDate.now());
    }

    @Test
    void testGetPrincipalUnauthenticated() throws Exception {
        List<Feedback> feedbacks = Arrays.asList(testFeedback);
        when(feedbackService.listarFeedbacks()).thenReturn(feedbacks);

        mockMvc.perform(get("/principal"))
                .andExpect(status().isOk())
                .andExpect(view().name("HTML/index"))
                .andExpect(model().attributeExists("feedbacks"))
                .andExpect(model().attribute("isAuthenticated", false))
                .andExpect(model().attributeDoesNotExist("userName"))
                .andExpect(model().attribute("feedbackEnviado", false));

        verify(feedbackService, times(1)).listarFeedbacks();
    }

    @Test
    @WithMockUser(username = "cliente@email.com")
    void testGetPrincipalAuthenticated() throws Exception {
        List<Feedback> feedbacks = Arrays.asList(testFeedback);
        when(userService.findRegisterUserByEmail("cliente@email.com")).thenReturn(testRegisterUser);
        when(feedbackService.listarFeedbacks()).thenReturn(feedbacks);

        mockMvc.perform(get("/principal"))
                .andExpect(status().isOk())
                .andExpect(view().name("HTML/index"))
                .andExpect(model().attributeExists("feedbacks"))
                .andExpect(model().attribute("isAuthenticated", true))
                .andExpect(model().attribute("userName", "Maria"))
                .andExpect(model().attribute("feedbackEnviado", false));

        verify(userService, times(1)).findRegisterUserByEmail("cliente@email.com");
        verify(feedbackService, times(1)).listarFeedbacks();
    }

    @Test
    @WithMockUser(username = "cliente@email.com")
    void testGetPrincipalWithFeedbackSuccess() throws Exception {
        when(userService.findRegisterUserByEmail("cliente@email.com")).thenReturn(testRegisterUser);
        when(feedbackService.listarFeedbacks()).thenReturn(Arrays.asList(testFeedback));

        mockMvc.perform(get("/principal")
                        .param("feedbackSuccess", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("HTML/index"))
                .andExpect(model().attribute("feedbackEnviado", true));

        verify(feedbackService, times(1)).listarFeedbacks();
    }

    @Test
    @WithMockUser(username = "cliente@email.com")
    void testSalvarFeedbackSuccess() throws Exception {
        when(userService.findRegisterUserByEmail("cliente@email.com")).thenReturn(testRegisterUser);
        doNothing().when(feedbackService).salvarFeedback(any(Feedback.class));

        mockMvc.perform(post("/feedback")
                        .with(csrf())
                        .param("comentario", "Ótimo atendimento!")
                        .param("rating", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/principal?feedbackSuccess=true"));

        verify(userService, times(1)).findRegisterUserByEmail("cliente@email.com");
        verify(feedbackService, times(1)).salvarFeedback(any(Feedback.class));
    }

    @Test
    @WithAnonymousUser
    void testSalvarFeedbackUnauthenticated() throws Exception {
        mockMvc.perform(post("/feedback")
                        .with(csrf())
                        .param("comentario", "Tentativa de feedback")
                        .param("rating", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(feedbackService, never()).salvarFeedback(any(Feedback.class));
    }

    @Test
    @WithMockUser(username = "cliente@email.com")
    void testSalvarFeedbackWithValidRating() throws Exception {
        when(userService.findRegisterUserByEmail("cliente@email.com")).thenReturn(testRegisterUser);

        mockMvc.perform(post("/feedback")
                        .with(csrf())
                        .param("comentario", "Muito bom!")
                        .param("rating", "4"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/principal?feedbackSuccess=true"));

        verify(feedbackService, times(1)).salvarFeedback(argThat(feedback ->
                feedback.getAvaliacao() == 4 &&
                        feedback.getComentario().equals("Muito bom!") &&
                        feedback.getCliente().equals(testRegisterUser) &&
                        feedback.getData().equals(LocalDate.now())
        ));
    }

    @Test
    @WithMockUser(username = "cliente@email.com")
    void testSalvarFeedbackSetsCorrectDate() throws Exception {
        when(userService.findRegisterUserByEmail("cliente@email.com")).thenReturn(testRegisterUser);

        mockMvc.perform(post("/feedback")
                        .with(csrf())
                        .param("comentario", "Feedback com data")
                        .param("rating", "5"))
                .andExpect(status().is3xxRedirection());

        verify(feedbackService, times(1)).salvarFeedback(argThat(feedback ->
                feedback.getData().equals(LocalDate.now())
        ));
    }

    @Test
    @WithMockUser(username = "cliente@email.com")
    void testSalvarFeedbackAssociatesCorrectUser() throws Exception {
        when(userService.findRegisterUserByEmail("cliente@email.com")).thenReturn(testRegisterUser);

        mockMvc.perform(post("/feedback")
                        .with(csrf())
                        .param("comentario", "Teste de associação")
                        .param("rating", "3"))
                .andExpect(status().is3xxRedirection());

        verify(feedbackService, times(1)).salvarFeedback(argThat(feedback ->
                feedback.getCliente().getId().equals(1L) &&
                        feedback.getCliente().getEmail().equals("cliente@email.com")
        ));
    }

    @Test
    @WithMockUser(username = "novocliente@email.com")
    void testSalvarFeedbackForDifferentUser() throws Exception {
        RegisterUser outroUsuario = new RegisterUser();
        outroUsuario.setId(2L);
        outroUsuario.setEmail("novocliente@email.com");
        outroUsuario.setNomeCompleto("João Santos");
        outroUsuario.setTelefone("11988888888");
        outroUsuario.setCpf("987.654.321-00");
        outroUsuario.setDataNascimento(java.time.LocalDate.of(1985, 5, 15));
        outroUsuario.setSenha("senha456");

        when(userService.findRegisterUserByEmail("novocliente@email.com")).thenReturn(outroUsuario);

        mockMvc.perform(post("/feedback")
                        .with(csrf())
                        .param("comentario", "Feedback de outro usuário")
                        .param("rating", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/principal?feedbackSuccess=true"));

        verify(userService, times(1)).findRegisterUserByEmail("novocliente@email.com");
        verify(feedbackService, times(1)).salvarFeedback(argThat(feedback ->
                feedback.getCliente().equals(outroUsuario)
        ));
    }
}
