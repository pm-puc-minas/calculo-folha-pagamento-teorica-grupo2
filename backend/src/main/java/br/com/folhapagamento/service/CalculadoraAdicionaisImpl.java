package br.com.folhapagamento.service;

import br.com.folhapagamento.enums.GrauInsalubridade;
import br.com.folhapagamento.model.Funcionario;
import br.com.folhapagamento.interfaces.ICalculadoraAdicionais;
import br.com.folhapagamento.service.abstracts.CalculadoraBase;
import org.springframework.stereotype.Service;

@Service
public class CalculadoraAdicionaisImpl extends CalculadoraBase implements ICalculadoraAdicionais {
    
    private static final double SALARIO_MINIMO = 1380.60;
    private static final double PERCENTUAL_PERICULOSIDADE = 0.30;
    
    @Override
    protected double executarCalculo(Funcionario funcionario) {
        double periculosidade = calcularPericulosidade(funcionario);
        double insalubridade = calcularInsalubridade(funcionario);
        return periculosidade + insalubridade;
    }
    
    @Override
    public String getNomeCalculadora() {
        return "Calculadora de Adicionais";
    }
    
    @Override
    public double calcularPericulosidade(Funcionario funcionario) {
        if (funcionario.isRecebePericulosidade()) {
            return arredondarMonetario(funcionario.getSalarioBruto() * PERCENTUAL_PERICULOSIDADE);
        }
        return 0.0;
    }
    
    @Override
    public double calcularInsalubridade(Funcionario funcionario) {
        GrauInsalubridade grau = GrauInsalubridade.fromString(funcionario.getGrauInsalubridade());
        if (grau != null) {
            return arredondarMonetario(SALARIO_MINIMO * grau.getPercentual());
        }
        return 0.0;
    }
}