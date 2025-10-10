package br.com.folhapagamento.model;

import br.com.folhapagamento.model.abstracts.FuncionarioBase;

public class FuncionarioPJ extends FuncionarioBase {
    
    private String cnpjEmpresa;
    private double percentualComissao;
    private boolean recebeValeAlimentacao;
    private double valorValeAlimentacao;
    
    public FuncionarioPJ() {
        super();
    }
    
    public FuncionarioPJ(String nome, String cpf, String cargo, double salarioBruto, String cnpjEmpresa, double percentualComissao, boolean recebeValeAlimentacao) {
        super(nome, cpf, cargo, salarioBruto);
        this.cnpjEmpresa = cnpjEmpresa;
        this.percentualComissao = percentualComissao;
        this.recebeValeAlimentacao = recebeValeAlimentacao;
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
        if (cnpjEmpresa == null || cnpjEmpresa.trim().isEmpty()) {
            return false;
        }
        return true;
    }
    
    @Override
    public double calcularBeneficios() {
        double beneficios = 0.0;
        
        if (recebeValeAlimentacao && valorValeAlimentacao > 0) {
            beneficios += valorValeAlimentacao * 22;
        }
        
        return beneficios;
    }
    
    @Override
    public double calcularDescontos() {
        return 0.0;
    }
    
    public double calcularValorTotalComComissoes() {
        return salarioBruto + (salarioBruto * (percentualComissao / 100.0));
    }
    
    public String getCnpjEmpresa() {
        return cnpjEmpresa;
    }
    
    public void setCnpjEmpresa(String cnpjEmpresa) {
        this.cnpjEmpresa = cnpjEmpresa;
    }
    
    public double getPercentualComissao() {
        return percentualComissao;
    }
    
    public void setPercentualComissao(double percentualComissao) {
        this.percentualComissao = percentualComissao;
    }
    
    public boolean isRecebeValeAlimentacao() {
        return recebeValeAlimentacao;
    }
    
    public void setRecebeValeAlimentacao(boolean recebeValeAlimentacao) {
        this.recebeValeAlimentacao = recebeValeAlimentacao;
    }
    
    public double getValorValeAlimentacao() {
        return valorValeAlimentacao;
    }
    
    public void setValorValeAlimentacao(double valorValeAlimentacao) {
        this.valorValeAlimentacao = valorValeAlimentacao;
    }
    
    @Override
    public String toString() {
        return "FuncionarioPJ{" +
               "nome='" + nome + '\'' +
               ", cpf='" + cpf + '\'' +
               ", cargo='" + cargo + '\'' +
               ", salarioBruto=" + String.format("%.2f", salarioBruto) +
               ", cnpjEmpresa='" + cnpjEmpresa + '\'' +
               ", percentualComissao=" + String.format("%.2f", percentualComissao) + "%" +
               ", recebeValeAlimentacao=" + (recebeValeAlimentacao ? "Sim" : "Não") +
               '}';
    }
}