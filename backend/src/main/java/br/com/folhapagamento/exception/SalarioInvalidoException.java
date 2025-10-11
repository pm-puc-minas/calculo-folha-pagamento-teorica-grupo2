package br.com.folhapagamento.exception;

public class SalarioInvalidoException extends RuntimeException {
    
    public SalarioInvalidoException(String mensagem) {
        super(mensagem);
    }
    
    public SalarioInvalidoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}

