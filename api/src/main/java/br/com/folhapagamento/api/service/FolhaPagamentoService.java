package br.com.folhapagamento.api.service;

import br.com.folhapagamento.api.dto.FuncionarioInputDTO;
import br.com.folhapagamento.api.dto.RelatorioFolhaDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Service
public class FolhaPagamentoService {

    private static final BigDecimal SALARIO_MINIMO = new BigDecimal("1380.60");

    public RelatorioFolhaDTO calcularFolha(FuncionarioInputDTO input) {
        RelatorioFolhaDTO relatorio = new RelatorioFolhaDTO();

        relatorio.setNomeFuncionario(input.getNome());
        relatorio.setCargo(input.getCargo());
        relatorio.setMesReferencia(YearMonth.now().format(DateTimeFormatter.ofPattern("MM/yyyy")));
        relatorio.setSalarioBruto(input.getSalarioBruto());

        relatorio.setSalarioPorHora(calcularSalarioHora(input));

        BigDecimal adicionalPericulosidade = calcularPericulosidade(input);
        BigDecimal adicionalInsalubridade = calcularInsalubridade(input);
        relatorio.setAdicionalPericulosidade(adicionalPericulosidade);
        relatorio.setAdicionalInsalubridade(adicionalInsalubridade);

        BigDecimal valeAlimentacao = calcularValeAlimentacao(input);
        relatorio.setValeAlimentacao(valeAlimentacao);

        BigDecimal baseCalculoPrincipais = input.getSalarioBruto()
            .add(adicionalPericulosidade)
            .add(adicionalInsalubridade);

        BigDecimal fgtsDoMes = baseCalculoPrincipais.multiply(new BigDecimal("0.08")).setScale(2, RoundingMode.HALF_UP);
        relatorio.setFgtsMes(fgtsDoMes);

        BigDecimal descontoVT = calcularDescontoValeTransporte(input);
        BigDecimal descontoINSS = calcularDescontoINSS(baseCalculoPrincipais);
        
        BigDecimal baseCalculoIRRF = baseCalculoPrincipais.subtract(descontoINSS);
        BigDecimal deducaoDependentes = new BigDecimal(input.getNumeroDependentes()).multiply(new BigDecimal("189.59"));
        baseCalculoIRRF = baseCalculoIRRF.subtract(deducaoDependentes);
        
        BigDecimal descontoIRRF = calcularDescontoIRRF(baseCalculoIRRF);

        relatorio.setDescontoValeTransporte(descontoVT);
        relatorio.setDescontoINSS(descontoINSS);
        relatorio.setDescontoIRRF(descontoIRRF);

        BigDecimal totalProventos = baseCalculoPrincipais.add(valeAlimentacao);
        BigDecimal totalDescontos = descontoVT.add(descontoINSS).add(descontoIRRF);
        BigDecimal salarioLiquido = totalProventos.subtract(totalDescontos);

        relatorio.setTotalProventos(totalProventos);
        relatorio.setTotalDescontos(totalDescontos);
        relatorio.setSalarioLiquido(salarioLiquido);

        return relatorio;
    }

    private BigDecimal calcularSalarioHora(FuncionarioInputDTO input) {
        BigDecimal horasSemanais = BigDecimal.valueOf(input.getHorasTrabalhadasPorDia() * input.getDiasTrabalhadosPorSemana());
        BigDecimal horasMensais = horasSemanais.multiply(new BigDecimal("5"));
        return input.getSalarioBruto().divide(horasMensais, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularPericulosidade(FuncionarioInputDTO input) {
        if (input.isRecebeAdicionalPericulosidade()) {
            return input.getSalarioBruto().multiply(new BigDecimal("0.30")).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal calcularInsalubridade(FuncionarioInputDTO input) {
        if (input.getGrauInsalubridade() == null || input.getGrauInsalubridade().isBlank()) {
            return BigDecimal.ZERO;
        }
        return switch (input.getGrauInsalubridade().toLowerCase()) {
            case "baixo" -> SALARIO_MINIMO.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
            case "medio" -> SALARIO_MINIMO.multiply(new BigDecimal("0.20")).setScale(2, RoundingMode.HALF_UP);
            case "alto" -> SALARIO_MINIMO.multiply(new BigDecimal("0.40")).setScale(2, RoundingMode.HALF_UP);
            default -> BigDecimal.ZERO;
        };
    }

    private BigDecimal calcularValeAlimentacao(FuncionarioInputDTO input) {
        BigDecimal valorTotal = input.getValorDiarioValeAlimentacao().multiply(new BigDecimal(input.getDiasUteisTrabalhados()));
        return valorTotal.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularDescontoValeTransporte(FuncionarioInputDTO input) {
        BigDecimal limiteDesconto = input.getSalarioBruto().multiply(new BigDecimal("0.06"));
        if (input.getValorValeTransporteGasto().compareTo(limiteDesconto) < 0) {
            return input.getValorValeTransporteGasto().setScale(2, RoundingMode.HALF_UP);
        }
        return limiteDesconto.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularDescontoINSS(BigDecimal baseCalculo) {
        BigDecimal tetoINSS = new BigDecimal("7507.49");
        BigDecimal salarioParaCalculo = baseCalculo.compareTo(tetoINSS) > 0 ? tetoINSS : baseCalculo;

        BigDecimal contribuicaoTotal = BigDecimal.ZERO;

        BigDecimal limiteFaixa1 = new BigDecimal("1302.00");
        if (salarioParaCalculo.compareTo(limiteFaixa1) > 0) {
            contribuicaoTotal = contribuicaoTotal.add(limiteFaixa1.multiply(new BigDecimal("0.075")));
        } else {
            contribuicaoTotal = contribuicaoTotal.add(salarioParaCalculo.multiply(new BigDecimal("0.075")));
            return contribuicaoTotal.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal limiteFaixa2 = new BigDecimal("2571.29");
        if (salarioParaCalculo.compareTo(limiteFaixa2) > 0) {
            BigDecimal baseFaixa2 = limiteFaixa2.subtract(limiteFaixa1);
            contribuicaoTotal = contribuicaoTotal.add(baseFaixa2.multiply(new BigDecimal("0.09")));
        } else {
            BigDecimal baseFaixa2 = salarioParaCalculo.subtract(limiteFaixa1);
            contribuicaoTotal = contribuicaoTotal.add(baseFaixa2.multiply(new BigDecimal("0.09")));
            return contribuicaoTotal.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal limiteFaixa3 = new BigDecimal("3856.94");
        if (salarioParaCalculo.compareTo(limiteFaixa3) > 0) {
            BigDecimal baseFaixa3 = limiteFaixa3.subtract(limiteFaixa2);
            contribuicaoTotal = contribuicaoTotal.add(baseFaixa3.multiply(new BigDecimal("0.12")));
        } else {
            BigDecimal baseFaixa3 = salarioParaCalculo.subtract(limiteFaixa2);
            contribuicaoTotal = contribuicaoTotal.add(baseFaixa3.multiply(new BigDecimal("0.12")));
            return contribuicaoTotal.setScale(2, RoundingMode.HALF_UP);
        }
        
        BigDecimal limiteFaixa4 = new BigDecimal("7507.49");
        if (salarioParaCalculo.compareTo(limiteFaixa4) <= 0) {
            BigDecimal baseFaixa4 = salarioParaCalculo.subtract(limiteFaixa3);
            contribuicaoTotal = contribuicaoTotal.add(baseFaixa4.multiply(new BigDecimal("0.14")));
        } else {
            BigDecimal baseFaixa4 = limiteFaixa4.subtract(limiteFaixa3);
            contribuicaoTotal = contribuicaoTotal.add(baseFaixa4.multiply(new BigDecimal("0.14")));
        }

        return contribuicaoTotal.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularDescontoIRRF(BigDecimal baseCalculo) {
        BigDecimal imposto;
        
        if (baseCalculo.compareTo(new BigDecimal("1903.98")) <= 0) {
            imposto = BigDecimal.ZERO;
        
        } else if (baseCalculo.compareTo(new BigDecimal("2826.65")) <= 0) {
            BigDecimal aliquota = new BigDecimal("0.075");
            BigDecimal deducao = new BigDecimal("142.80");
            imposto = baseCalculo.multiply(aliquota).subtract(deducao);
        
        } else if (baseCalculo.compareTo(new BigDecimal("3751.05")) <= 0) {
            BigDecimal aliquota = new BigDecimal("0.15");
            BigDecimal deducao = new BigDecimal("354.80");
            imposto = baseCalculo.multiply(aliquota).subtract(deducao);
        
        } else if (baseCalculo.compareTo(new BigDecimal("4664.68")) <= 0) {
            BigDecimal aliquota = new BigDecimal("0.225");
            BigDecimal deducao = new BigDecimal("636.13");
            imposto = baseCalculo.multiply(aliquota).subtract(deducao);
        
        } else {
            BigDecimal aliquota = new BigDecimal("0.275");
            BigDecimal deducao = new BigDecimal("869.36");
            imposto = baseCalculo.multiply(aliquota).subtract(deducao);
        }

        return imposto.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : imposto.setScale(2, RoundingMode.HALF_UP);
    }
}