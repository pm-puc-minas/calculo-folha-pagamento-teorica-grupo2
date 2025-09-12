package br.com.folhapagamento.api.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class FuncionarioInputDTO {
    private String nome;
    private String cargo;
    private BigDecimal salarioBruto;
    private boolean recebeAdicionalPericulosidade;
    private String grauInsalubridade;
    private BigDecimal valorValeTransporteGasto;
    private BigDecimal valorDiarioValeAlimentacao;
    private int diasUteisTrabalhados;
    private int numeroDependentes;
    private double horasTrabalhadasPorDia;
    private int diasTrabalhadosPorSemana;
}
