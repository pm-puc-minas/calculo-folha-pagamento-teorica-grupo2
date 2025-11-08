package br.com.folhapagamento.event;

import br.com.folhapagamento.model.entity.FolhaPagamentoEntity;

public class FolhaPagamentoGeradaEvent {
    
    private final FolhaPagamentoEntity folhaPagamento;
    private final String mensagem;
    
    public FolhaPagamentoGeradaEvent(FolhaPagamentoEntity folhaPagamento, String mensagem) {
        this.folhaPagamento = folhaPagamento;
        this.mensagem = mensagem;
    }
    
    public FolhaPagamentoEntity getFolhaPagamento() {
        return folhaPagamento;
    }
    
    public String getMensagem() {
        return mensagem;
    }
}

