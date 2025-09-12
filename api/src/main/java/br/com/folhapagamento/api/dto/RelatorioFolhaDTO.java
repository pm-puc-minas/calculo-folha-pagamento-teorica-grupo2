package br.com.folhapagamento.api.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class RelatorioFolhaDTO {
    private String nomeFuncionario;
    private String cargo;
    private String mesReferencia;

    private BigDecimal salarioBruto;
    private BigDecimal totalProventos;
    private BigDecimal totalDescontos;
    private BigDecimal salarioLiquido;
    private BigDecimal salarioPorHora;

    private BigDecimal adicionalPericulosidade = BigDecimal.ZERO;
    private BigDecimal adicionalInsalubridade = BigDecimal.ZERO;
    private BigDecimal valeAlimentacao = BigDecimal.ZERO;

    private BigDecimal descontoValeTransporte = BigDecimal.ZERO;
    private BigDecimal descontoINSS = BigDecimal.ZERO;
    private BigDecimal descontoIRRF = BigDecimal.ZERO;

    private BigDecimal fgtsMes = BigDecimal.ZERO;
}
