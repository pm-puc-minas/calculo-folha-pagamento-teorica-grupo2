package br.com.folhapagamento.interfaces;

public interface ICalculadoraDescontos {
    double calcularINSS(double baseCalculo);
    double calcularIRRF(double baseCalculo, int dependentes);
    double calcularFGTS(double baseCalculo);
}

