package com.barbearia.Barbearia.Model;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class RegisterUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome completo não pode estar vazio")
    @Size(min = 2, max = 200, message = "O nome completo deve ter entre 1 e 200 caracteres")
    @Column(nullable = false, length = 200)
    private String nomeCompleto;

    @NotBlank(message = "O telefone não pode estar vazio")
    @Column(nullable = false)
    private String telefone;

    @NotBlank(message = "O CPF não pode estar vazio")
    @Size(min = 14, max = 14, message = "O CPF deve ter 14 caracteres")
    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @NotNull(message = "A data de nascimento não pode estar vazia")
    @Column(nullable = false)
    private LocalDate dataNascimento;

    @NotBlank(message = "O email não pode estar vazio")
    @Column(nullable = false, unique = true, length = 200)
    private String email;

    @NotBlank(message = "A senha não pode estar vazia")
    @Column(nullable = false)
    private String senha;

    @Column(name = "foto")
    private String foto; // Armazena em Base64

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Long getId() {
        return id;
    }   

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

}