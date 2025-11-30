package com.barbearia.Barbearia.Model;

import java.time.LocalDate;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "feedbacks")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotBlank(message = "O comentário não pode estar vazio")
    @Size(min = 2, max = 200, message = "O comentário deve ter entre 2 e 200 caracteres")
    private String comentario;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    @NotNull(message = "A avaliação não pode estar vazia")
    private Integer avaliacao;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private RegisterUser cliente;

    public Feedback() {
        this.data = LocalDate.now();
    }
    
    public Feedback(Long id, String comentario, RegisterUser cliente, LocalDate data, Integer avaliacao) {
        this.id = id;
        this.comentario = comentario;
        this.cliente = cliente;
        this.data = data;
        this.avaliacao = avaliacao;
    }

    public RegisterUser getCliente() {
        return cliente;
    }

    public void setCliente(RegisterUser cliente) {
        this.cliente = cliente;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Integer getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Integer avaliacao) {
        this.avaliacao = avaliacao;
    }

}