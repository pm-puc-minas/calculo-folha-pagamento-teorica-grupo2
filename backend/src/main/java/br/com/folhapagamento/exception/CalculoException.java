package br.com.folhapagamento.exception;

public class CalculoException extends RuntimeException {
    
    public CalculoException(String mensagem) {
        super(mensagem);
    }
    
    public CalculoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}

