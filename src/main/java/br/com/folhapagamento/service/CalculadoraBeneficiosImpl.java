package br.com.folhapagamento.service;

import br.com.folhapagamento.model.Funcionario;
import br.com.folhapagamento.interfaces.CalculadoraBeneficios;
import org.springframework.stereotype.Service;

@Service
public class CalculadoraBeneficiosImpl implements CalculadoraBeneficios {
    
    private static final double LIMITE_VALE_TRANSPORTE = 0.06;
    
    @Override
    public double calcularValeAlimentacao(Funcionario funcionario) {
        return funcionario.getValorValeAlimentacao() * 22;
    }
    
    @Override
    public double calcularDescontoValeTransporte(Funcionario funcionario) {
        double limite = funcionario.getSalarioBruto() * LIMITE_VALE_TRANSPORTE;
        if (funcionario.getValorValeTransporte() <= limite) {
            return funcionario.getValorValeTransporte();
        } else {
            return limite;
        }
    }
}
