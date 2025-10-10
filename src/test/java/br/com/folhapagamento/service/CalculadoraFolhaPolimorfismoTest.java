package br.com.folhapagamento.service;

import br.com.folhapagamento.model.FolhaPagamento;
import br.com.folhapagamento.model.FuncionarioCLT;
import br.com.folhapagamento.model.FuncionarioPJ;
import br.com.folhapagamento.model.abstracts.FuncionarioBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalculadoraFolhaPolimorfismoTest {
    
    private CalculadoraFolha calculadoraFolha;
    private FuncionarioCLT funcionarioCLT;
    private FuncionarioPJ funcionarioPJ;
    
    @BeforeEach
    void setUp() {
        calculadoraFolha = new CalculadoraFolha();
        
        funcionarioCLT = new FuncionarioCLT();
        funcionarioCLT.setNome("João CLT");
        funcionarioCLT.setCpf("11144477735");
        funcionarioCLT.setCargo("Desenvolvedor");
        funcionarioCLT.setSalarioBruto(3000.0);
        funcionarioCLT.setNumeroDependentes(1);
        funcionarioCLT.setRecebePericulosidade(false);
        funcionarioCLT.setGrauInsalubridade("");
        funcionarioCLT.setValorValeTransporte(150.0);
        funcionarioCLT.setValorValeAlimentacao(25.0);
        
        funcionarioPJ = new FuncionarioPJ();
        funcionarioPJ.setNome("Maria PJ");
        funcionarioPJ.setCpf("11144477735");
        funcionarioPJ.setCargo("Consultora");
        funcionarioPJ.setSalarioBruto(5000.0);
        funcionarioPJ.setCnpjEmpresa("12345678000195");
        funcionarioPJ.setPercentualComissao(5.0);
        funcionarioPJ.setRecebeValeAlimentacao(true);
        funcionarioPJ.setValorValeAlimentacao(30.0);
    }
    
    @Test
    void testCalcularFolhaPolimorfica_CLT() {
        FolhaPagamento folha = calculadoraFolha.calcularFolhaPolimorfica(funcionarioCLT);
        
        assertNotNull(folha);
        assertEquals(funcionarioCLT.getSalarioBruto(), folha.getSalarioBruto());
        assertTrue(folha.getSalarioLiquido() > 0);
    }
    
    @Test
    void testCalcularFolhaPolimorfica_PJ() {
        FolhaPagamento folha = calculadoraFolha.calcularFolhaPolimorfica(funcionarioPJ);
        
        assertNotNull(folha);
        assertEquals(funcionarioPJ.getSalarioBruto(), folha.getSalarioBruto());
        assertTrue(folha.getSalarioLiquido() > 0);
    }
    
    @Test
    void testCalcularFolhasEmLote() {
        List<FuncionarioBase> funcionarios = new ArrayList<>();
        funcionarios.add(funcionarioCLT);
        funcionarios.add(funcionarioPJ);
        
        List<FolhaPagamento> folhas = calculadoraFolha.calcularFolhasEmLote(funcionarios);
        
        assertNotNull(folhas);
        assertEquals(2, folhas.size());
        
        FolhaPagamento folhaCLT = folhas.get(0);
        FolhaPagamento folhaPJ = folhas.get(1);
        
        assertEquals(funcionarioCLT.getSalarioBruto(), folhaCLT.getSalarioBruto());
        assertEquals(funcionarioPJ.getSalarioBruto(), folhaPJ.getSalarioBruto());
    }
    
    @Test
    void testPolimorfismo_ProcessamentoDiferente() {
        FuncionarioCLT clt = new FuncionarioCLT("Teste CLT", "111", "Cargo", 1000.0);
        FuncionarioPJ pj = new FuncionarioPJ("Teste PJ", "222", "Cargo", 2000.0, "CNPJ", 0.0, false);
        
        FolhaPagamento folhaCLT = calculadoraFolha.calcularFolhaPolimorfica(clt);
        FolhaPagamento folhaPJ = calculadoraFolha.calcularFolhaPolimorfica(pj);
        
        assertNotNull(folhaCLT);
        assertNotNull(folhaPJ);
        assertNotEquals(folhaCLT.getSalarioLiquido(), folhaPJ.getSalarioLiquido());
    }
    
    @Test
    void testHeranca_FuncionarioBase() {
        assertTrue(funcionarioCLT instanceof FuncionarioBase);
        assertTrue(funcionarioPJ instanceof FuncionarioBase);
    }
    
    @Test
    void testPolimorfismo_CalculoEspecifico() {
        double salarioLiquidoCLT = funcionarioCLT.calcularSalarioLiquido();
        double salarioLiquidoPJ = funcionarioPJ.calcularSalarioLiquido();
        
        assertTrue(salarioLiquidoCLT > 0);
        assertTrue(salarioLiquidoPJ > 0);
        assertNotEquals(salarioLiquidoCLT, salarioLiquidoPJ);
    }
}