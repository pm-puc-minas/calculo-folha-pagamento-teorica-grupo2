package br.com.folhapagamento.model;

import br.com.folhapagamento.enums.GrauInsalubridade;
import br.com.folhapagamento.model.abstracts.FuncionarioBase;

import java.time.LocalDate;

public class FuncionarioCLT extends FuncionarioBase {
    
    private int numeroDependentes;
    private boolean recebePericulosidade;
    private String grauInsalubridade;
    private double valorValeTransporte;
    private double valorValeAlimentacao;
    
    public FuncionarioCLT() {
        super();
    }
    
    public FuncionarioCLT(String nome, String cpf, String cargo, double salarioBruto) {
        super(nome, cpf, cargo, salarioBruto);
        this.numeroDependentes = 0;
        this.recebePericulosidade = false;
        this.grauInsalubridade = "";
        this.valorValeTransporte = 0.0;
        this.valorValeAlimentacao = 0.0;
    }
    
    @Override
    public boolean validarFuncionario() {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }
        if (!validarCPF(cpf)) {
            return false;
        }
        if (salarioBruto <= 0) {
            return false;
        }
        return true;
    }
    
    @Override
    public double calcularBeneficios() {
        double beneficios = 0.0;
        
        if (valorValeAlimentacao > 0) {
            beneficios += valorValeAlimentacao * 22;
        }
        
        return beneficios;
    }
    
    @Override
    public double calcularDescontos() {
        double descontos = 0.0;
        
        if (valorValeTransporte > 0) {
            double limiteVT = salarioBruto * 0.06;
            descontos += Math.min(valorValeTransporte, limiteVT);
        }
        
        return descontos;
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
    
    @Override
    public String toString() {
        return "FuncionarioCLT{" +
               "nome='" + nome + '\'' +
               ", cpf='" + cpf + '\'' +
               ", cargo='" + cargo + '\'' +
               ", salarioBruto=" + String.format("%.2f", salarioBruto) +
               ", numeroDependentes=" + numeroDependentes +
               ", recebePericulosidade=" + (recebePericulosidade ? "Sim" : "Não") +
               ", grauInsalubridade='" + grauInsalubridade + '\'' +
               ", valorValeTransporte=" + String.format("%.2f", valorValeTransporte) +
               ", valorValeAlimentacao=" + String.format("%.2f", valorValeAlimentacao) +
               '}';
    }
}