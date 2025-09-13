package br.com.folhapagamento.service;

import br.com.folhapagamento.enums.GrauInsalubridade;
import br.com.folhapagamento.model.Funcionario;
import br.com.folhapagamento.interfaces.CalculadoraAdicionais;
import org.springframework.stereotype.Service;

@Service
public class CalculadoraAdicionaisImpl implements CalculadoraAdicionais {
    
    private static final double SALARIO_MINIMO = 1380.60;
    private static final double PERCENTUAL_PERICULOSIDADE = 0.30;
    
    @Override
    public double calcularPericulosidade(Funcionario funcionario) {
        if (funcionario.isRecebePericulosidade()) {
            return funcionario.getSalarioBruto() * PERCENTUAL_PERICULOSIDADE;
        }
        return 0.0;
    }
    
    @Override
    public double calcularInsalubridade(Funcionario funcionario) {
        GrauInsalubridade grau = GrauInsalubridade.fromString(funcionario.getGrauInsalubridade());
        if (grau != null) {
            return SALARIO_MINIMO * grau.getPercentual();
        }
        return 0.0;
    }
}
