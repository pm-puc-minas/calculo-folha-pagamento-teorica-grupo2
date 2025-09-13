package br.com.folhapagamento.service;

import br.com.folhapagamento.interfaces.CalculadoraSalario;
import org.springframework.stereotype.Service;

@Service
public class CalculadoraSalarioImpl implements CalculadoraSalario {
    
    @Override
    public double calcularSalarioHora(double salarioBruto) {
        return salarioBruto / 200.0;
    }
}
