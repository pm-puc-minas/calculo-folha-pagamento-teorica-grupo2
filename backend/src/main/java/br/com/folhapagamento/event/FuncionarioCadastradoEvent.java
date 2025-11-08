package br.com.folhapagamento.event;

import br.com.folhapagamento.model.entity.FuncionarioEntity;


public class FuncionarioCadastradoEvent {
    
    private final FuncionarioEntity funcionario;
    private final String acaoRealizada;
    
    public FuncionarioCadastradoEvent(FuncionarioEntity funcionario, String acaoRealizada) {
        this.funcionario = funcionario;
        this.acaoRealizada = acaoRealizada;
    }
    
    public FuncionarioEntity getFuncionario() {
        return funcionario;
    }
    
    public String getAcaoRealizada() {
        return acaoRealizada;
    }
}
