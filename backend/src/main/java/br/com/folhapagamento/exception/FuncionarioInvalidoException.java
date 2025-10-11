package br.com.folhapagamento.exception;

public class FuncionarioInvalidoException extends RuntimeException {
    
    public FuncionarioInvalidoException(String mensagem) {
        super(mensagem);
    }
    
    public FuncionarioInvalidoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}

