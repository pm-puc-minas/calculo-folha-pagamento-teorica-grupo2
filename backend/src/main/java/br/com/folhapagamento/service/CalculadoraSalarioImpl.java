package br.com.folhapagamento.service;

import br.com.folhapagamento.interfaces.ICalculadoraSalario;
import br.com.folhapagamento.model.Funcionario;
import br.com.folhapagamento.service.abstracts.CalculadoraBase;
import org.springframework.stereotype.Service;

@Service
public class CalculadoraSalarioImpl extends CalculadoraBase implements ICalculadoraSalario {
    
    private static final double HORAS_TRABALHADAS_MES = 200.0;
    
    @Override
    protected double executarCalculo(Funcionario funcionario) {
        return calcularSalarioHora(funcionario.getSalarioBruto());
    }
    
    @Override
    public String getNomeCalculadora() {
        return "Calculadora de Salário";
    }
    
    @Override
    public double calcularSalarioHora(double salarioBruto) {
        return arredondarMonetario(salarioBruto / HORAS_TRABALHADAS_MES);
    }
}