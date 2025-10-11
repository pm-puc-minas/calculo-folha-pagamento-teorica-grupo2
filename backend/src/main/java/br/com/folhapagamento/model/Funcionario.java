package br.com.folhapagamento.model;

public class Funcionario {
    
    private String nome;
    private String cpf;
    private String cargo;
    private double salarioBruto;
    private int numeroDependentes;
    private boolean recebePericulosidade;
    private String grauInsalubridade;
    private double valorValeTransporte;
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
