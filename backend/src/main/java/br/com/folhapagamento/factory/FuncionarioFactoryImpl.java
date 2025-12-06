package br.com.folhapagamento.factory;

import br.com.folhapagamento.model.Funcionario;
import br.com.folhapagamento.model.FuncionarioCLT;
import br.com.folhapagamento.model.FuncionarioPJ;
import br.com.folhapagamento.model.abstracts.FuncionarioBase;
import org.springframework.stereotype.Component;

@Component
public class FuncionarioFactoryImpl implements FuncionarioFactory {
    
    @Override
    public FuncionarioBase criarFuncionario(String tipo, Funcionario funcionario) {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo de funcionário não pode ser nulo ou vazio");
        }
        
        if (funcionario == null) {
            throw new IllegalArgumentException("Dados do funcionário não podem ser nulos");
        }
        
        return switch (tipo.toUpperCase().trim()) {
            case "CLT" -> criarFuncionarioCLT(funcionario);
            case "PJ" -> criarFuncionarioPJ(funcionario);
            default -> throw new IllegalArgumentException(
                "Tipo de funcionário inválido: '" + tipo + "'. Tipos válidos: CLT, PJ"
            );
        };
    }
    
    private FuncionarioCLT criarFuncionarioCLT(Funcionario funcionario) {
        FuncionarioCLT clt = new FuncionarioCLT();
        
        clt.setNome(funcionario.getNome());
        clt.setCpf(funcionario.getCpf());
        clt.setCargo(funcionario.getCargo());
        clt.setSalarioBruto(funcionario.getSalarioBruto());
        clt.setNumeroDependentes(funcionario.getNumeroDependentes());
        clt.setRecebePericulosidade(funcionario.isRecebePericulosidade());
        clt.setGrauInsalubridade(funcionario.getGrauInsalubridade());
        clt.setValorValeTransporte(funcionario.getValorValeTransporte());
        clt.setValorValeAlimentacao(funcionario.getValorValeAlimentacao());
        
        return clt;
    }
    
    private FuncionarioPJ criarFuncionarioPJ(Funcionario funcionario) {
        FuncionarioPJ pj = new FuncionarioPJ();
        
        pj.setNome(funcionario.getNome());
        pj.setCpf(funcionario.getCpf());
        pj.setCargo(funcionario.getCargo());
        pj.setSalarioBruto(funcionario.getSalarioBruto());
        pj.setValorValeAlimentacao(funcionario.getValorValeAlimentacao());
        
        return pj;
    }
    
    public boolean isTipoValido(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            return false;
        }
        String tipoUpper = tipo.toUpperCase().trim();
        return "CLT".equals(tipoUpper) || "PJ".equals(tipoUpper);
    }
}
