package br.com.folhapagamento.service;

import br.com.folhapagamento.interfaces.CalculadoraBeneficios;
import br.com.folhapagamento.model.Funcionario;
import br.com.folhapagamento.service.abstracts.CalculadoraBase;
import org.springframework.stereotype.Service;

@Service
public class CalculadoraBeneficiosImpl extends CalculadoraBase implements CalculadoraBeneficios {
    
    private static final double PERCENTUAL_VALE_TRANSPORTE = 0.06;
    private static final double VALOR_MAXIMO_VALE_ALIMENTACAO = 550.00;
    
    @Override
    protected double executarCalculo(Funcionario funcionario) {
        double valeAlimentacao = calcularValeAlimentacao(funcionario);
        double descontoValeTransporte = calcularDescontoValeTransporte(funcionario);
        
        return valeAlimentacao - descontoValeTransporte;
    }
    
    @Override
    public String getNomeCalculadora() {
        return "Calculadora de Benefícios";
    }
    
    @Override
    public double calcularValeAlimentacao(Funcionario funcionario) {
        if (funcionario.getValorValeAlimentacao() > 0) {
            return arredondarMonetario(funcionario.getValorValeAlimentacao() * 22);
        }
        return 0.0;
    }
    
    @Override
    public double calcularDescontoValeTransporte(Funcionario funcionario) {
        if (funcionario.getValorValeTransporte() > 0) {
            double limiteVT = funcionario.getSalarioBruto() * PERCENTUAL_VALE_TRANSPORTE;
            return arredondarMonetario(Math.min(funcionario.getValorValeTransporte(), limiteVT));
        }
        return 0.0;
    }
}