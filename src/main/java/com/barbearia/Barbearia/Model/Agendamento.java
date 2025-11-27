package com.barbearia.Barbearia.Model;

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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "agendamentos")
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O barbeiro não pode ser nulo.")
    @ManyToOne
    @JoinColumn(name = "barbeiro_id", nullable = false)
    private Barbeiro barbeiro;

    @NotNull(message = "O cliente não pode ser nulo.")
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private RegisterUser cliente;

    @NotBlank(message = "O status não pode estar vazio.")
    @Column(nullable = false)
    private String status;

    @NotNull(message = "O campo doador não pode ser nulo.")
    @Column(nullable = false)
    private Boolean doador;

    @Column(name = "servico", nullable = false, length = 500)
    private String servico;

    @Column(name = "foto", columnDefinition = "TEXT")
    private String foto;

    @NotNull(message = "A data não pode ser nula.")
    @Column(nullable = false)
    private LocalDate data;

    @NotNull(message = "A hora não pode ser nula.")
    @Column(nullable = false)
    private LocalTime hora;

    @NotNull(message = "O valor não pode ser nulo.")
    @Column(nullable = false)
    private Double valor;

    public Agendamento() {
    }

    public Agendamento(Long id, Barbeiro barbeiro, RegisterUser cliente, String status, Boolean doador, String foto,
            String servico, LocalDate data, LocalTime hora, Double valor) {
        this.id = id;
        this.barbeiro = barbeiro;
        this.cliente = cliente;
        this.status = status;
        this.doador = doador;
        this.foto = foto;
        this.servico = servico;
        this.data = data;
        this.hora = hora;
        this.valor = valor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Barbeiro getBarbeiro() {
        return barbeiro;
    }

    public void setBarbeiro(Barbeiro barbeiro) {
        this.barbeiro = barbeiro;
    }

    public RegisterUser getCliente() {
        return cliente;
    }

    public void setCliente(RegisterUser cliente) {
        this.cliente = cliente;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getDoador() {
        return doador;
    }

    public void setDoador(Boolean doador) {
        this.doador = doador;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getServico() {
        return servico;
    }

    public void setServico(String servico) {
        this.servico = servico;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

}
