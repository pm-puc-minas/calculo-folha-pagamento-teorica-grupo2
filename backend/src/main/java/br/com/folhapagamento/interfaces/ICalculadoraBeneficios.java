package br.com.folhapagamento.interfaces;

import br.com.folhapagamento.model.Funcionario;

public interface ICalculadoraBeneficios {
    double calcularValeAlimentacao(Funcionario funcionario);
    double calcularDescontoValeTransporte(Funcionario funcionario);
}

