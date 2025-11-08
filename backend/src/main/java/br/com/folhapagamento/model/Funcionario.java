package br.com.folhapagamento.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tb_funcionarios")
public class Funcionario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;
    
    private String cpf;
    
    @NotBlank(message = "Cargo é obrigatório")
    @Size(min = 2, max = 50, message = "Cargo deve ter entre 2 e 50 caracteres")
    private String cargo;

    @NotBlank(message = "Departamento é obrigatório")
    private String departamento;
    
    @NotBlank(message = "Tipo de funcionário é obrigatório")
    private String tipo;
    
    @NotNull(message = "Salário bruto é obrigatório")
    @Positive(message = "Salário bruto deve ser maior que zero")
    @DecimalMin(value = "1320.00", message = "Salário bruto não pode ser menor que o salário mínimo (R$ 1.320,00)")
    @DecimalMax(value = "100000.00", message = "Salário bruto não pode exceder R$ 100.000,00")
    private double salarioBruto;
    
    @Min(value = 0, message = "Número de dependentes não pode ser negativo")
    @Max(value = 20, message = "Número de dependentes não pode exceder 20")
    private int numeroDependentes;
    
    private boolean recebePericulosidade;
    
    private String grauInsalubridade;
    
    @PositiveOrZero(message = "Valor do vale transporte não pode ser negativo")
    @DecimalMax(value = "10000.00", message = "Valor do vale transporte não pode exceder R$ 10.000,00")
    private double valorValeTransporte;
    
    @PositiveOrZero(message = "Valor do vale alimentação não pode ser negativo")
    @DecimalMax(value = "10000.00", message = "Valor do vale alimentação não pode exceder R$ 10.000,00")
    private double valorValeAlimentacao;
    
    public Funcionario() {
    }
    
    public Funcionario(String nome, String cpf, String cargo, double salarioBruto) {
        this.nome = nome;
        this.cpf = cpf;
        this.cargo = cargo;
        this.salarioBruto = salarioBruto;
        this.numeroDependentes = 0;
        this.recebePericulosidade = false;
        this.grauInsalubridade = "";
        this.valorValeTransporte = 0.0;
        this.valorValeAlimentacao = 0.0;
    }

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

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public double getSalarioBruto() {
        return salarioBruto;
    }
    
    public void setSalarioBruto(double salarioBruto) {
        this.salarioBruto = salarioBruto;
    }
    
    public int getNumeroDependentes() {
        return numeroDependentes;
    }
    
    public void setNumeroDependentes(int numeroDependentes) {
        this.numeroDependentes = numeroDependentes;
    }
    
    public boolean isRecebePericulosidade() {
        return recebePericulosidade;
    }
    
    public void setRecebePericulosidade(boolean recebePericulosidade) {
        this.recebePericulosidade = recebePericulosidade;
    }
    
    public String getGrauInsalubridade() {
        return grauInsalubridade;
    }
    
    public void setGrauInsalubridade(String grauInsalubridade) {
        this.grauInsalubridade = grauInsalubridade;
    }
    
    public double getValorValeTransporte() {
        return valorValeTransporte;
    }
    
    public void setValorValeTransporte(double valorValeTransporte) {
        this.valorValeTransporte = valorValeTransporte;
    }
    
    public double getValorValeAlimentacao() {
        return valorValeAlimentacao;
    }
    
    public void setValorValeAlimentacao(double valorValeAlimentacao) {
        this.valorValeAlimentacao = valorValeAlimentacao;
    }
}