package br.com.folhapagamento.interfaces;

public interface CalculadoraDescontos {
    double calcularINSS(double baseCalculo);
    double calcularIRRF(double baseCalculo, int dependentes);
    double calcularFGTS(double baseCalculo);
}
