package br.com.folhapagamento.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "funcionarios")
public class FuncionarioEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nome;
    
    @Column(unique = true, length = 14)
    private String cpf;
    
    @NotBlank(message = "Cargo é obrigatório")
    @Size(min = 2, max = 50, message = "Cargo deve ter entre 2 e 50 caracteres")
    @Column(nullable = false, length = 50)
    private String cargo;
    
    @NotBlank(message = "Tipo de funcionário é obrigatório")
    @Column(nullable = false, length = 10)
    private String tipo;
    
    @NotNull(message = "Salário bruto é obrigatório")
    @Positive(message = "Salário bruto deve ser maior que zero")
    @DecimalMin(value = "1320.00", message = "Salário bruto não pode ser menor que o salário mínimo")
    @DecimalMax(value = "100000.00", message = "Salário bruto não pode exceder R$ 100.000,00")
    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double salarioBruto;
    
    @Min(value = 0, message = "Número de dependentes não pode ser negativo")
    @Max(value = 20, message = "Número de dependentes não pode exceder 20")
    @Column(nullable = false)
    private Integer numeroDependentes = 0;
    
    @Column(nullable = false)
    private Boolean recebePericulosidade = false;
    
    @Column(length = 20)
    private String grauInsalubridade;
    
    @PositiveOrZero(message = "Valor do vale transporte não pode ser negativo")
    @DecimalMax(value = "10000.00", message = "Valor do vale transporte não pode exceder R$ 10.000,00")
    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0.0")
    private Double valorValeTransporte = 0.0;
    
    @PositiveOrZero(message = "Valor do vale alimentação não pode ser negativo")
    @DecimalMax(value = "10000.00", message = "Valor do vale alimentação não pode exceder R$ 10.000,00")
    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0.0")
    private Double valorValeAlimentacao = 0.0;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;
    
    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;
    
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
    
    public FuncionarioEntity() {
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getCpf() {
        return cpf;
    }
    
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    
    public String getCargo() {
        return cargo;
    }
    
    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public Double getSalarioBruto() {
        return salarioBruto;
    }
    
    public void setSalarioBruto(Double salarioBruto) {
        this.salarioBruto = salarioBruto;
    }
    
    public Integer getNumeroDependentes() {
        return numeroDependentes;
    }
    
    public void setNumeroDependentes(Integer numeroDependentes) {
        this.numeroDependentes = numeroDependentes;
    }
    
    public Boolean getRecebePericulosidade() {
        return recebePericulosidade;
    }
    
    public void setRecebePericulosidade(Boolean recebePericulosidade) {
        this.recebePericulosidade = recebePericulosidade;
    }
    
    public String getGrauInsalubridade() {
        return grauInsalubridade;
    }
    
    public void setGrauInsalubridade(String grauInsalubridade) {
        this.grauInsalubridade = grauInsalubridade;
    }
    
    public Double getValorValeTransporte() {
        return valorValeTransporte;
    }
    
    public void setValorValeTransporte(Double valorValeTransporte) {
        this.valorValeTransporte = valorValeTransporte;
    }
    
    public Double getValorValeAlimentacao() {
        return valorValeAlimentacao;
    }
    
    public void setValorValeAlimentacao(Double valorValeAlimentacao) {
        this.valorValeAlimentacao = valorValeAlimentacao;
    }
    
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
    
    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    
    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }
    
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
}

