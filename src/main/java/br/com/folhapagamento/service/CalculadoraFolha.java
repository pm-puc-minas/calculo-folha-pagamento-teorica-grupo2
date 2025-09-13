package br.com.folhapagamento.service;

import br.com.folhapagamento.interfaces.CalculadoraSalario;
import br.com.folhapagamento.interfaces.CalculadoraAdicionais;
import br.com.folhapagamento.interfaces.CalculadoraBeneficios;
import br.com.folhapagamento.interfaces.CalculadoraDescontos;
import br.com.folhapagamento.interfaces.FolhaPagamentoService;
import br.com.folhapagamento.model.FolhaPagamento;
import br.com.folhapagamento.model.Funcionario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CalculadoraFolha implements FolhaPagamentoService {
    
    @Autowired
    private CalculadoraSalario calculadoraSalario;
    
    @Autowired
    private CalculadoraAdicionais calculadoraAdicionais;
    
    @Autowired
    private CalculadoraBeneficios calculadoraBeneficios;
    
    @Autowired
    private CalculadoraDescontos calculadoraDescontos;
    
    @Override
    public FolhaPagamento calcularFolha(Funcionario funcionario) {
        FolhaPagamento folha = new FolhaPagamento();
        folha.setFuncionario(funcionario);
        folha.setSalarioBruto(funcionario.getSalarioBruto());
        
        folha.setSalarioPorHora(calculadoraSalario.calcularSalarioHora(funcionario.getSalarioBruto()));
        
        folha.setAdicionalPericulosidade(calculadoraAdicionais.calcularPericulosidade(funcionario));
        folha.setAdicionalInsalubridade(calculadoraAdicionais.calcularInsalubridade(funcionario));
        
        folha.setValeAlimentacao(calculadoraBeneficios.calcularValeAlimentacao(funcionario));
        folha.setDescontoValeTransporte(calculadoraBeneficios.calcularDescontoValeTransporte(funcionario));
        
        double baseCalculo = funcionario.getSalarioBruto() + 
                           folha.getAdicionalPericulosidade() + 
                           folha.getAdicionalInsalubridade();
        
        folha.setDescontoINSS(calculadoraDescontos.calcularINSS(baseCalculo));
        folha.setDescontoIRRF(calculadoraDescontos.calcularIRRF(baseCalculo, funcionario.getNumeroDependentes()));
        
        folha.setFgts(calculadoraDescontos.calcularFGTS(baseCalculo));
        
        folha.setTotalAntesDescontos(baseCalculo + folha.getValeAlimentacao());
        folha.setTotalDescontos(folha.getDescontoValeTransporte() + 
                               folha.getDescontoINSS() + 
                               folha.getDescontoIRRF());
        folha.setSalarioLiquido(folha.getTotalAntesDescontos() - folha.getTotalDescontos());
        
        return folha;
    }
}
