package br.com.folhapagamento.interfaces;

import br.com.folhapagamento.model.Funcionario;

public interface CalculadoraBeneficios {
    double calcularValeAlimentacao(Funcionario funcionario);
    double calcularDescontoValeTransporte(Funcionario funcionario);
}
