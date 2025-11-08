package br.com.folhapagamento.service;

import br.com.folhapagamento.interfaces.ICalculadoraDescontos;
import br.com.folhapagamento.model.Funcionario;
import br.com.folhapagamento.service.abstracts.CalculadoraBase;
import org.springframework.stereotype.Service;

@Service
public class CalculadoraDescontosImpl extends CalculadoraBase implements ICalculadoraDescontos {
    
    private static final double FAIXA1_INSS = 1320.00;
    private static final double FAIXA2_INSS = 2571.29;
    private static final double FAIXA3_INSS = 3856.94;
    private static final double FAIXA4_INSS = 7507.49;
    
    private static final double ALIQUOTA1_INSS = 0.075;
    private static final double ALIQUOTA2_INSS = 0.09;
    private static final double ALIQUOTA3_INSS = 0.12;
    private static final double ALIQUOTA4_INSS = 0.14;
    
    private static final double FAIXA1_IRRF = 1903.98;
    private static final double FAIXA2_IRRF = 2826.65;
    private static final double FAIXA3_IRRF = 3751.05;
    private static final double FAIXA4_IRRF = 4664.68;
    
    private static final double ALIQUOTA1_IRRF = 0.0;
    private static final double ALIQUOTA2_IRRF = 0.075;
    private static final double ALIQUOTA3_IRRF = 0.15;
    private static final double ALIQUOTA4_IRRF = 0.225;
    private static final double ALIQUOTA5_IRRF = 0.275;
    
    private static final double DEDUCAO1_IRRF = 0.0;
    private static final double DEDUCAO2_IRRF = 142.80;
    private static final double DEDUCAO3_IRRF = 354.80;
    private static final double DEDUCAO4_IRRF = 636.13;
    private static final double DEDUCAO5_IRRF = 869.36;
    
    private static final double VALOR_POR_DEPENDENTE = 189.59;
    private static final double PERCENTUAL_FGTS = 0.08;
    
    @Override
    protected double executarCalculo(Funcionario funcionario) {
        double baseCalculo = funcionario.getSalarioBruto();
        double inss = calcularINSS(baseCalculo);
        double irrf = calcularIRRF(baseCalculo, funcionario.getNumeroDependentes());
        return inss + irrf;
    }
    
    @Override
    public String getNomeCalculadora() {
        return "Calculadora de Descontos";
    }
    
    @Override
    public double calcularINSS(double baseCalculo) {
        double inss = 0.0;
        
        if (baseCalculo <= FAIXA1_INSS) {
            inss = baseCalculo * ALIQUOTA1_INSS;
        } else if (baseCalculo <= FAIXA2_INSS) {
            inss = (FAIXA1_INSS * ALIQUOTA1_INSS) + 
                   ((baseCalculo - FAIXA1_INSS) * ALIQUOTA2_INSS);
        } else if (baseCalculo <= FAIXA3_INSS) {
            inss = (FAIXA1_INSS * ALIQUOTA1_INSS) + 
                   ((FAIXA2_INSS - FAIXA1_INSS) * ALIQUOTA2_INSS) + 
                   ((baseCalculo - FAIXA2_INSS) * ALIQUOTA3_INSS);
        } else {
            inss = (FAIXA1_INSS * ALIQUOTA1_INSS) + 
                   ((FAIXA2_INSS - FAIXA1_INSS) * ALIQUOTA2_INSS) + 
                   ((FAIXA3_INSS - FAIXA2_INSS) * ALIQUOTA3_INSS) + 
                   ((baseCalculo - FAIXA3_INSS) * ALIQUOTA4_INSS);
        }
        
        return arredondarMonetario(inss);
    }
    
    @Override
    public double calcularIRRF(double baseCalculo, int dependentes) {
        double baseIRRF = baseCalculo - calcularINSS(baseCalculo) - (dependentes * VALOR_POR_DEPENDENTE);
        double irrf = 0.0;
        
        if (baseIRRF <= FAIXA1_IRRF) {
            irrf = baseIRRF * ALIQUOTA1_IRRF - DEDUCAO1_IRRF;
        } else if (baseIRRF <= FAIXA2_IRRF) {
            irrf = baseIRRF * ALIQUOTA2_IRRF - DEDUCAO2_IRRF;
        } else if (baseIRRF <= FAIXA3_IRRF) {
            irrf = baseIRRF * ALIQUOTA3_IRRF - DEDUCAO3_IRRF;
        } else if (baseIRRF <= FAIXA4_IRRF) {
            irrf = baseIRRF * ALIQUOTA4_IRRF - DEDUCAO4_IRRF;
        } else {
            irrf = baseIRRF * ALIQUOTA5_IRRF - DEDUCAO5_IRRF;
        }
        
        return arredondarMonetario(Math.max(0.0, irrf));
    }
    
    @Override
    public double calcularFGTS(double baseCalculo) {
        return arredondarMonetario(baseCalculo * PERCENTUAL_FGTS);
    }
}