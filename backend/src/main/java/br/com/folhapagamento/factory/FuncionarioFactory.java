package br.com.folhapagamento.factory;

import br.com.folhapagamento.model.Funcionario;
import br.com.folhapagamento.model.abstracts.FuncionarioBase;

public interface FuncionarioFactory {
    
    FuncionarioBase criarFuncionario(String tipo, Funcionario funcionario);
}
