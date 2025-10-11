package br.com.folhapagamento.exception;

public class DependentesInvalidosException extends RuntimeException {
    
    public DependentesInvalidosException(String mensagem) {
        super(mensagem);
    }
    
    public DependentesInvalidosException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}

