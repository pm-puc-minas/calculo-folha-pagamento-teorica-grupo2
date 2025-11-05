package br.com.folhapagamento.model.abstracts;

import java.time.LocalDate;
import java.util.Objects;

//Boa! Classe abstrata seguindo nomenclatura clara de Base
public abstract class FuncionarioBase {
    
    protected String nome;
    protected String cpf;
    protected String cargo;
    protected double salarioBruto;
    protected LocalDate dataAdmissao;
    protected boolean ativo;
    
    public FuncionarioBase() {
        this.ativo = true;
        this.dataAdmissao = LocalDate.now();
    }
    
    public FuncionarioBase(String nome, String cpf, String cargo, double salarioBruto) {
        this();
        this.nome = nome;
        this.cpf = cpf;
        this.cargo = cargo;
        this.salarioBruto = salarioBruto;
    }
    
    public abstract boolean validarFuncionario();
    
    public abstract double calcularBeneficios();
    
    public abstract double calcularDescontos();
    
    public final double calcularSalarioLiquido() {
        if (!validarFuncionario()) {
            throw new IllegalStateException("Funcionário inválido para cálculo de salário");
        }
        
        double beneficios = calcularBeneficios();
        double descontos = calcularDescontos();
        
        return salarioBruto + beneficios - descontos;
    }
    
    protected boolean validarCPF(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return false;
        }
        
        cpf = cpf.replaceAll("\\D", "");
        
        if (cpf.length() != 11) {
            return false;
        }
        
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }
        
        try {
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            }
            int primeiroDigito = 11 - (soma % 11);
            if (primeiroDigito >= 10) primeiroDigito = 0;
            
            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
            }
            int segundoDigito = 11 - (soma % 11);
            if (segundoDigito >= 10) segundoDigito = 0;
            
            return primeiroDigito == Character.getNumericValue(cpf.charAt(9)) &&
                   segundoDigito == Character.getNumericValue(cpf.charAt(10));
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getCpfFormatado() {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + "." + 
               cpf.substring(3, 6) + "." + 
               cpf.substring(6, 9) + "-" + 
               cpf.substring(9, 11);
    }
    
    public String getInformacoesBasicas() {
        return String.format("Nome: %s, CPF: %s, Cargo: %s, Salário: R$ %.2f", 
                           nome, getCpfFormatado(), cargo, salarioBruto);
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
    
    public double getSalarioBruto() {
        return salarioBruto;
    }
    
    public void setSalarioBruto(double salarioBruto) {
        this.salarioBruto = salarioBruto;
    }
    
    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }
    
    public void setDataAdmissao(LocalDate dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }
    
    public boolean isAtivo() {
        return ativo;
    }
    
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FuncionarioBase that = (FuncionarioBase) o;
        return Objects.equals(cpf, that.cpf);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(cpf);
    }
    
    @Override
    public String toString() {
        return getInformacoesBasicas();
    }
}