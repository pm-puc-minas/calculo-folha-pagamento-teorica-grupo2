package br.com.folhapagamento.model;

public class FolhaPagamento {
    
    private Funcionario funcionario;
    private double salarioBruto;
    private double salarioPorHora;
    private double adicionalPericulosidade;
    private double adicionalInsalubridade;
    private double valeAlimentacao;
    private double descontoValeTransporte;
    private double descontoINSS;
    private double descontoIRRF;
    private double fgts;
    private double totalAntesDescontos;
    private double totalDescontos;
    private double salarioLiquido;
    
    public FolhaPagamento() {
    }
    public Funcionario getFuncionario() {
        return funcionario;
    }
    
    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }
    
    public double getSalarioBruto() {
        return salarioBruto;
    }
    
    public void setSalarioBruto(double salarioBruto) {
        this.salarioBruto = salarioBruto;
    }
    
    public double getSalarioPorHora() {
        return salarioPorHora;
    }
    
    public void setSalarioPorHora(double salarioPorHora) {
        this.salarioPorHora = salarioPorHora;
    }
    
    public double getAdicionalPericulosidade() {
        return adicionalPericulosidade;
    }
    
    public void setAdicionalPericulosidade(double adicionalPericulosidade) {
        this.adicionalPericulosidade = adicionalPericulosidade;
    }
    
    public double getAdicionalInsalubridade() {
        return adicionalInsalubridade;
    }
    
    public void setAdicionalInsalubridade(double adicionalInsalubridade) {
        this.adicionalInsalubridade = adicionalInsalubridade;
    }
    
    public double getValeAlimentacao() {
        return valeAlimentacao;
    }
    
    public void setValeAlimentacao(double valeAlimentacao) {
        this.valeAlimentacao = valeAlimentacao;
    }
    
    public double getDescontoValeTransporte() {
        return descontoValeTransporte;
    }
    
    public void setDescontoValeTransporte(double descontoValeTransporte) {
        this.descontoValeTransporte = descontoValeTransporte;
    }
    
    public double getDescontoINSS() {
        return descontoINSS;
    }
    
    public void setDescontoINSS(double descontoINSS) {
        this.descontoINSS = descontoINSS;
    }
    
    public double getDescontoIRRF() {
        return descontoIRRF;
    }
    
    public void setDescontoIRRF(double descontoIRRF) {
        this.descontoIRRF = descontoIRRF;
    }
    
    public double getFgts() {
        return fgts;
    }
    
    public void setFgts(double fgts) {
        this.fgts = fgts;
    }
    
    public double getTotalAntesDescontos() {
        return totalAntesDescontos;
    }
    
    public void setTotalAntesDescontos(double totalAntesDescontos) {
        this.totalAntesDescontos = totalAntesDescontos;
    }
    
    public double getTotalDescontos() {
        return totalDescontos;
    }
    
    public void setTotalDescontos(double totalDescontos) {
        this.totalDescontos = totalDescontos;
    }
    
    public double getSalarioLiquido() {
        return salarioLiquido;
    }
    
    public void setSalarioLiquido(double salarioLiquido) {
        this.salarioLiquido = salarioLiquido;
    }
}
