package com.barbearia.Barbearia.Model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "barbeiros")
public class Barbeiro {

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

    @NotBlank(message = "A especialidade não pode estar vazia")
    @Size(min = 2, max = 200, message = "A especialidade deve ter entre 2 e 200 caracteres")
    @Column(nullable = false, unique = true, length = 200)
    private String especialidade;

    @Column(name = "biografia", columnDefinition = "TEXT")
    private String biografia; // MUDOU: agora é String!

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

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
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
