package br.com.folhapagamento.interfaces;

import br.com.folhapagamento.model.Funcionario;

//Interfaces em Java devem começar com I: ICalculadoraAdicionais
public interface CalculadoraAdicionais {
    double calcularPericulosidade(Funcionario funcionario);
    double calcularInsalubridade(Funcionario funcionario);
}
