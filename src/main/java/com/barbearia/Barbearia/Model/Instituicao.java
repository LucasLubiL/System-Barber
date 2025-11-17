package com.barbearia.Barbearia.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@Entity
public class Instituicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 1, max = 200, message = "O nome da insituição deve ter entre 1 e 200 caracteres")
    @NotBlank(message = "O nome da instituição não pode estar vazio")
    @NotNull
    @Column(nullable = false, length = 200)
    private String nomeInstituicao;

    @Size(min = 14, max = 14, message = "O CNPJ deve ter 14 caracteres")
    @NotBlank(message = "O CNPJ não pode estar vazio")
    @NotNull
    @Column(nullable = false, length = 14)
    private String cnpj;

    @Size(min = 1, max = 200, message = "O nome do responsável deve ter entre 1 e 200 caracteres")
    @NotBlank(message = "O nome do responsável não pode estar vazio")
    @NotNull
    @Column(nullable = false, length = 200)
    private String responsavel;

    @Size(min = 1, max = 200, message = "O cargo do responsável deve ter entre 1 e 200 caracteres")
    @NotBlank(message = "O cargo do responsável não pode estar vazio")
    @NotNull
    @Column(nullable = false, length = 200)
    private String cargoResponsavel;

    @Size(min = 1, max = 200, message = "O email deve ter entre 1 e 200 caracteres")
    @NotBlank(message = "O email não pode estar vazio")
    @NotNull
    @Column(nullable = false, length = 200)
    private String email;

    @Size(min = 1, max = 10, message = "O telefone deve ter entre 1 e 10 caracteres")
    @NotBlank(message = "O telefone não pode estar vazio")
    @NotNull
    @Column(nullable = false, length = 10)
    private String telefone;

    @Size(min = 1, max = 200, message = "O endereço da insituição deve ter entre 1 e 200 caracteres")
    @NotBlank(message = "O endereço da instituição não pode estar vazio")
    @NotNull
    @Column(nullable = false, length = 200)
    private String endereco;

    @Size(min = 1, max = 100, message = "O nome da cidade deve ter entre 1 e 100 caracteres")
    @NotBlank(message = "O nome da cidade não pode estar vazio")
    @NotNull
    @Column(nullable = false, length = 100)
    private String cidade;

    @Size(min = 1, max = 100, message = "O nome do estado deve ter entre 1 e 100 caracteres")
    @NotBlank(message = "O nome do estado não pode estar vazio")
    @NotNull
    @Column(nullable = false, length = 100)
    private String estado;

    @Size(min = 1, max = 50, message = "O tipo da instituição deve ter entre 1 e 50 caracteres")
    @NotBlank(message = "O tipo da instituição não pode estar vazio")
    @NotNull
    @Column(nullable = false, length = 50)
    private String tipoInstituicao;

    @Size(min = 1, max = 1000, message = "A missão da instituição deve ter entre 1 e 1000 caracteres")
    @NotBlank(message = "A missão da instituição não pode estar vazia")
    @NotNull
    @Column(nullable = false, length = 1000)
    private String missaoInstituicao;

    @Size(min = 1, max = 200, message = "O publico beneficiado deve ter entre 1 e 200 caracteres")
    @NotBlank(message = "O publico beneficiado não pode estar vazio")
    @NotNull
    @Column(nullable = false, length = 200)
    private String publicoBeneficiado;

    @Size(min = 1, max = 50, message = "Voce deve selecionar uma das opções")
    @NotBlank(message = "Voce deve selecionar uma das opções")
    @NotNull
    @Column(nullable = false, length = 50)
    private String comoUsar;

    @Size(min = 0, max = 1000, message = "A informação adicional só pode ter até 1000 caracteres")
    @Column(length = 1000)
    private String infoAdicional;

    public void save(Instituicao instituicao) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

}
