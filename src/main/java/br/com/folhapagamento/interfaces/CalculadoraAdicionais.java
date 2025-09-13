package br.com.folhapagamento.interfaces;

import br.com.folhapagamento.model.Funcionario;

public interface CalculadoraAdicionais {
    double calcularPericulosidade(Funcionario funcionario);
    double calcularInsalubridade(Funcionario funcionario);
}
