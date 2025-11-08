package main.java.br.com.folhapagamento.event;

import main.java.br.com.folhapagamento.model.Funcionario;

public record FuncionarioCadastradoEvent(
    Funcionario funcionario
) {
}