package br.com.folhapagamento.service;

import br.com.folhapagamento.exception.SalarioInvalidoException;
import br.com.folhapagamento.exception.DependentesInvalidosException;
import br.com.folhapagamento.exception.FuncionarioInvalidoException;
import br.com.folhapagamento.exception.CalculoException;
import br.com.folhapagamento.interfaces.ICalculadoraSalario;
import br.com.folhapagamento.interfaces.ICalculadoraAdicionais;
import br.com.folhapagamento.interfaces.ICalculadoraBeneficios;
import br.com.folhapagamento.interfaces.ICalculadoraDescontos;
import br.com.folhapagamento.interfaces.IFolhaPagamentoService;
import br.com.folhapagamento.model.FolhaPagamento;
import br.com.folhapagamento.model.Funcionario;
import br.com.folhapagamento.model.FuncionarioCLT;
import br.com.folhapagamento.model.FuncionarioPJ;
import br.com.folhapagamento.model.abstracts.FuncionarioBase;
import br.com.folhapagamento.service.abstracts.CalculadoraBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CalculadoraFolha implements IFolhaPagamentoService {
    
    private static final Logger logger = LoggerFactory.getLogger(CalculadoraFolha.class);
    private static final double SALARIO_MINIMO = 1320.00;
    private static final double PERCENTUAL_MAX_VALE_TRANSPORTE = 0.06;
    
    @Autowired
    private ICalculadoraSalario calculadoraSalario;
    
    @Autowired
    private ICalculadoraAdicionais calculadoraAdicionais;
    
    @Autowired
    private ICalculadoraBeneficios calculadoraBeneficios;
    
    @Autowired
    private ICalculadoraDescontos calculadoraDescontos;
    
    @Override
    public FolhaPagamento calcularFolha(Funcionario funcionario) {
        // Validações de negócio
        validarFuncionario(funcionario);
        
        try {
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
        
            logger.info("Folha calculada com sucesso para funcionário: {}", funcionario.getNome());
            return folha;
            
        } catch (Exception e) {
            logger.error("Erro ao calcular folha para funcionário: {}", funcionario.getNome(), e);
            throw new CalculoException("Erro ao processar o cálculo da folha de pagamento", e);
        }
    }
    
    /**
     * Valida os dados do funcionário antes de calcular a folha
     */
    private void validarFuncionario(Funcionario funcionario) {
        if (funcionario == null) {
            throw new FuncionarioInvalidoException("Funcionário não pode ser nulo");
        }
        
        // Valida nome
        if (funcionario.getNome() == null || funcionario.getNome().trim().isEmpty()) {
            throw new FuncionarioInvalidoException("Nome do funcionário é obrigatório");
        }
        
        // Valida cargo
        if (funcionario.getCargo() == null || funcionario.getCargo().trim().isEmpty()) {
            throw new FuncionarioInvalidoException("Cargo do funcionário é obrigatório");
        }
        
        // Valida salário
        if (funcionario.getSalarioBruto() <= 0) {
            throw new SalarioInvalidoException("Salário bruto deve ser maior que zero");
        }
        
        if (funcionario.getSalarioBruto() < SALARIO_MINIMO) {
            throw new SalarioInvalidoException(
                String.format("Salário bruto (R$ %.2f) não pode ser menor que o salário mínimo (R$ %.2f)", 
                    funcionario.getSalarioBruto(), SALARIO_MINIMO)
            );
        }
        
        // Valida dependentes
        if (funcionario.getNumeroDependentes() < 0) {
            throw new DependentesInvalidosException("Número de dependentes não pode ser negativo");
        }
        
        if (funcionario.getNumeroDependentes() > 20) {
            throw new DependentesInvalidosException("Número de dependentes não pode exceder 20");
        }
        
        // Valida vale transporte
        if (funcionario.getValorValeTransporte() < 0) {
            throw new FuncionarioInvalidoException("Valor do vale transporte não pode ser negativo");
        }
        
        double limiteValeTransporte = funcionario.getSalarioBruto() * PERCENTUAL_MAX_VALE_TRANSPORTE;
        if (funcionario.getValorValeTransporte() > limiteValeTransporte) {
            throw new FuncionarioInvalidoException(
                String.format("Valor do vale transporte (R$ %.2f) não pode exceder 6%% do salário (R$ %.2f)", 
                    funcionario.getValorValeTransporte(), limiteValeTransporte)
            );
        }
        
        // Valida vale alimentação
        if (funcionario.getValorValeAlimentacao() < 0) {
            throw new FuncionarioInvalidoException("Valor do vale alimentação não pode ser negativo");
        }
        
        logger.debug("Validação de funcionário concluída: {}", funcionario.getNome());
    }
    
    public FolhaPagamento calcularFolhaPolimorfica(FuncionarioBase funcionarioBase) {
        FolhaPagamento folha = new FolhaPagamento();
        
        Funcionario funcionario = converterParaFuncionario(funcionarioBase);
        folha.setFuncionario(funcionario);
        folha.setSalarioBruto(funcionarioBase.getSalarioBruto());
        
        if (funcionarioBase instanceof FuncionarioCLT) {
            return calcularFolhaCLT((FuncionarioCLT) funcionarioBase, folha);
        } else if (funcionarioBase instanceof FuncionarioPJ) {
            return calcularFolhaPJ((FuncionarioPJ) funcionarioBase, folha);
        } else {
            return calcularFolha(funcionario);
        }
    }
    
    private FolhaPagamento calcularFolhaCLT(FuncionarioCLT funcionarioCLT, FolhaPagamento folha) {
        folha.setSalarioPorHora(calculadoraSalario.calcularSalarioHora(funcionarioCLT.getSalarioBruto()));
        
        Funcionario funcionarioCLTConvertido = converterParaFuncionario(funcionarioCLT);
        
        folha.setAdicionalPericulosidade(calculadoraAdicionais.calcularPericulosidade(funcionarioCLTConvertido));
        folha.setAdicionalInsalubridade(calculadoraAdicionais.calcularInsalubridade(funcionarioCLTConvertido));
        
        folha.setValeAlimentacao(calculadoraBeneficios.calcularValeAlimentacao(funcionarioCLTConvertido));
        folha.setDescontoValeTransporte(calculadoraBeneficios.calcularDescontoValeTransporte(funcionarioCLTConvertido));
        
        double baseCalculo = funcionarioCLT.getSalarioBruto() + 
                           folha.getAdicionalPericulosidade() + 
                           folha.getAdicionalInsalubridade();
        
        folha.setDescontoINSS(calculadoraDescontos.calcularINSS(baseCalculo));
        folha.setDescontoIRRF(calculadoraDescontos.calcularIRRF(baseCalculo, funcionarioCLT.getNumeroDependentes()));
        folha.setFgts(calculadoraDescontos.calcularFGTS(baseCalculo));
        
        folha.setTotalAntesDescontos(baseCalculo + folha.getValeAlimentacao());
        folha.setTotalDescontos(folha.getDescontoValeTransporte() + 
                               folha.getDescontoINSS() + 
                               folha.getDescontoIRRF());
        folha.setSalarioLiquido(folha.getTotalAntesDescontos() - folha.getTotalDescontos());
        
        return folha;
    }
    
    private FolhaPagamento calcularFolhaPJ(FuncionarioPJ funcionarioPJ, FolhaPagamento folha) {
        folha.setSalarioPorHora(calculadoraSalario.calcularSalarioHora(funcionarioPJ.getSalarioBruto()));
        
        folha.setAdicionalPericulosidade(0.0);
        folha.setAdicionalInsalubridade(0.0);
        
        Funcionario funcionarioPJConvertido = converterParaFuncionario(funcionarioPJ);
        
        folha.setValeAlimentacao(calculadoraBeneficios.calcularValeAlimentacao(funcionarioPJConvertido));
        folha.setDescontoValeTransporte(0.0);
        
        folha.setDescontoINSS(0.0);
        folha.setDescontoIRRF(0.0);
        folha.setFgts(0.0);
        
        folha.setTotalAntesDescontos(funcionarioPJ.getSalarioBruto() + folha.getValeAlimentacao());
        folha.setTotalDescontos(0.0);
        folha.setSalarioLiquido(folha.getTotalAntesDescontos());
        
        return folha;
    }
    
    private Funcionario converterParaFuncionario(FuncionarioBase funcionarioBase) {
        Funcionario funcionario = new Funcionario();
        funcionario.setNome(funcionarioBase.getNome());
        funcionario.setCpf(funcionarioBase.getCpf());
        funcionario.setCargo(funcionarioBase.getCargo());
        funcionario.setSalarioBruto(funcionarioBase.getSalarioBruto());
        
        if (funcionarioBase instanceof FuncionarioCLT) {
            FuncionarioCLT clt = (FuncionarioCLT) funcionarioBase;
            funcionario.setNumeroDependentes(clt.getNumeroDependentes());
            funcionario.setRecebePericulosidade(clt.isRecebePericulosidade());
            funcionario.setGrauInsalubridade(clt.getGrauInsalubridade());
            funcionario.setValorValeTransporte(clt.getValorValeTransporte());
            funcionario.setValorValeAlimentacao(clt.getValorValeAlimentacao());
        } else if (funcionarioBase instanceof FuncionarioPJ) {
            FuncionarioPJ pj = (FuncionarioPJ) funcionarioBase;
            funcionario.setNumeroDependentes(0);
            funcionario.setRecebePericulosidade(false);
            funcionario.setGrauInsalubridade("");
            funcionario.setValorValeTransporte(0.0);
            funcionario.setValorValeAlimentacao(pj.getValorValeAlimentacao());
        }
        
        return funcionario;
    }
    
    public List<FolhaPagamento> calcularFolhasEmLote(List<FuncionarioBase> funcionarios) {
        List<FolhaPagamento> folhas = new ArrayList<>();
        
        for (FuncionarioBase funcionario : funcionarios) {
            FolhaPagamento folha = calcularFolhaPolimorfica(funcionario);
            folhas.add(folha);
        }
        
        return folhas;
    }
}