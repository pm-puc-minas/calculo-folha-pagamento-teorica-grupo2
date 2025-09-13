package br.com.folhapagamento.service;

import br.com.folhapagamento.interfaces.CalculadoraDescontos;
import org.springframework.stereotype.Service;

@Service
public class CalculadoraDescontosImpl implements CalculadoraDescontos {
    
    private static final double PERCENTUAL_FGTS = 0.08;
    private static final double DEDUCAO_DEPENDENTE = 189.59;
    
    @Override
    public double calcularINSS(double baseCalculo) {
        if (baseCalculo <= 1302.00) {
            return baseCalculo * 0.075;
        } else if (baseCalculo <= 2571.29) {
            return baseCalculo * 0.09;
        } else if (baseCalculo <= 3856.94) {
            return baseCalculo * 0.12;
        } else {
            return baseCalculo * 0.14;
        }
    }
    
    @Override
    public double calcularIRRF(double baseCalculo, int dependentes) {
        double baseIRRF = baseCalculo - (dependentes * DEDUCAO_DEPENDENTE);
        
        if (baseIRRF <= 1903.98) {
            return 0.0;
        } else if (baseIRRF <= 2826.65) {
            return (baseIRRF * 0.075) - 142.80;
        } else if (baseIRRF <= 3751.05) {
            return (baseIRRF * 0.15) - 354.80;
        } else if (baseIRRF <= 4664.68) {
            return (baseIRRF * 0.225) - 636.13;
        } else {
            return (baseIRRF * 0.275) - 869.36;
        }
    }
    
    @Override
    public double calcularFGTS(double baseCalculo) {
        return baseCalculo * PERCENTUAL_FGTS;
    }
}
