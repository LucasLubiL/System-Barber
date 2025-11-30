package com.barbearia.Barbearia.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {

        @Autowired
        private UserDetailsService uds;

        @Autowired
        private BCryptPasswordEncoder encoder;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

                http.authorizeHttpRequests(requests -> requests
                                // PÁGINAS PÚBLICAS (não precisa estar logado)
                                .requestMatchers("/", "/home", "/instituicao", "/instituicao/save", "/login",
                                                "/register", "/saveUser",
                                                "/CSS/**", "/JS/**", "/images/**", "/img/**", "/servicos/**", "/uploads/**")
                                .permitAll()

                                // ✅ ROTAS COMPARTILHADAS (CLIENTE E BARBEIRO) - DEVEM VIR ANTES
                                .requestMatchers("/cancelarAgendamento", "/agendamento/concluir")
                                .hasAnyAuthority("ROLE_CLIENT", "ROLE_BARBER")

                                // PÁGINAS PARA CLIENTES (ROLE_CLIENT) - SEM /cancelarAgendamento
                                .requestMatchers("/cliente/**", "/agendamentoClient/**", "/saveAgendamento")
                                .hasAuthority("ROLE_CLIENT")

                                // PÁGINAS PARA BARBEIROS (ROLE_BARBER)
                                .requestMatchers("/barbeiro/**", "/agendamentoBarber/**")
                                .hasAuthority("ROLE_BARBER")

                                // PÁGINAS PARA ADMIN (ROLE_ADMIN)
                                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")

                                // QUALQUER OUTRA ROTA EXIGE AUTENTICAÇÃO
                                .anyRequest().authenticated())

                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers("/instituicao/save", "/saveAgendamento",
                                                                "/cancelarAgendamento", "/agendamento/concluir"))

                                .formLogin(login -> login
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login")
                                                .defaultSuccessUrl("/home", true)
                                                .failureUrl("/login?error=true")
                                                .permitAll())

                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/home")
                                                .permitAll())

                                .exceptionHandling(handling -> handling
                                                .accessDeniedPage("/accessDenied"))

                                .authenticationProvider(authenticationProvider(uds, encoder));

                return http.build();
        }

        @Bean
        public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                        PasswordEncoder passwordEncoder) {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
                authProvider.setPasswordEncoder(passwordEncoder);
                return authProvider;
        }
}