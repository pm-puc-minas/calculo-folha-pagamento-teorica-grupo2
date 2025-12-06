package br.com.folhapagamento.config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfiguracaoFiscal {
    
    private static volatile ConfiguracaoFiscal instance;
    
    private final double salarioMinimo;
    private final int horasMensais;
    private final double percentualFgts;
    private final double percentualPericulosidade;
    private final double limiteValeTransporte;
    private final int diasUteisValeAlimentacao;
    private final Map<String, Double> tabelaInsalubridade;
    private final Map<Double, Double> tabelaInss;
    private final Map<Double, double[]> tabelaIrrf;
    private final double deducaoPorDependente;
    
    private ConfiguracaoFiscal() {
        this.salarioMinimo = 1412.00;
        this.horasMensais = 220;
        this.percentualFgts = 0.08;
        this.percentualPericulosidade = 0.30;
        this.limiteValeTransporte = 0.06;
        this.diasUteisValeAlimentacao = 22;
        this.deducaoPorDependente = 189.59;
        
        Map<String, Double> insalubridade = new LinkedHashMap<>();
        insalubridade.put("MINIMO", 0.10);
        insalubridade.put("MEDIO", 0.20);
        insalubridade.put("MAXIMO", 0.40);
        this.tabelaInsalubridade = Collections.unmodifiableMap(insalubridade);

        Map<Double, Double> inss = new LinkedHashMap<>();
        inss.put(1412.00, 0.075);
        inss.put(2666.68, 0.09);
        inss.put(4000.03, 0.12);
        inss.put(7786.02, 0.14);
        this.tabelaInss = Collections.unmodifiableMap(inss);
     
        Map<Double, double[]> irrf = new LinkedHashMap<>();
        irrf.put(2259.20, new double[]{0.0, 0.0});
        irrf.put(2826.65, new double[]{0.075, 169.44});
        irrf.put(3751.05, new double[]{0.15, 381.44});
        irrf.put(4664.68, new double[]{0.225, 662.77});
        irrf.put(Double.MAX_VALUE, new double[]{0.275, 896.00});
        this.tabelaIrrf = Collections.unmodifiableMap(irrf);
    }
    
    public static ConfiguracaoFiscal getInstance() {
        if (instance == null) {
            synchronized (ConfiguracaoFiscal.class) {
                if (instance == null) {
                    instance = new ConfiguracaoFiscal();
                }
            }
        }
        return instance;
    }
    
    public double getSalarioMinimo() {
        return salarioMinimo;
    }
    
    public int getHorasMensais() {
        return horasMensais;
    }
    
    public double getPercentualFgts() {
        return percentualFgts;
    }
    
    public double getPercentualPericulosidade() {
        return percentualPericulosidade;
    }
    
    public double getLimiteValeTransporte() {
        return limiteValeTransporte;
    }
    
    public int getDiasUteisValeAlimentacao() {
        return diasUteisValeAlimentacao;
    }
    
    public double getDeducaoPorDependente() {
        return deducaoPorDependente;
    }
    
    public Map<String, Double> getTabelaInsalubridade() {
        return tabelaInsalubridade;
    }
    
    public Map<Double, Double> getTabelaInss() {
        return tabelaInss;
    }
    
    public Map<Double, double[]> getTabelaIrrf() {
        return tabelaIrrf;
    }
    
    public double getPercentualInsalubridade(String grau) {
        return tabelaInsalubridade.getOrDefault(grau.toUpperCase(), 0.0);
    }
    
    public double calcularInsalubridade(String grau) {
        double percentual = getPercentualInsalubridade(grau);
        return salarioMinimo * percentual;
    }
}

