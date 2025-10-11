package br.com.folhapagamento.service.abstracts;

import br.com.folhapagamento.model.Funcionario;
import org.springframework.stereotype.Component;

@Component
public abstract class CalculadoraBase {
    
    public final double calcular(Funcionario funcionario) {
        validarFuncionario(funcionario);
        double resultado = executarCalculo(funcionario);
        return aplicarRegrasNegocio(resultado, funcionario);
    }
    
    protected abstract double executarCalculo(Funcionario funcionario);
    
    protected void validarFuncionario(Funcionario funcionario) {
        if (funcionario == null) {
            throw new IllegalArgumentException("Funcionário não pode ser nulo");
        }
        if (funcionario.getSalarioBruto() <= 0) {
            throw new IllegalArgumentException("Salário bruto deve ser maior que zero");
        }
    }
    
    protected double aplicarRegrasNegocio(double resultado, Funcionario funcionario) {
        return Math.max(0.0, resultado);
    }
    
    protected double arredondarMonetario(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
    
    public abstract String getNomeCalculadora();
}
