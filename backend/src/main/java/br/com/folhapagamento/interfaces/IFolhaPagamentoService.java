package br.com.folhapagamento.interfaces;

import br.com.folhapagamento.model.FolhaPagamento;
import br.com.folhapagamento.model.Funcionario;

public interface IFolhaPagamentoService {
    FolhaPagamento calcularFolha(Funcionario funcionario);
}

