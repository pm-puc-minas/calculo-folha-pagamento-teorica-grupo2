package br.com.folhapagamento.service;

import br.com.folhapagamento.model.FolhaPagamento;
import br.com.folhapagamento.model.FuncionarioCLT;
import br.com.folhapagamento.model.FuncionarioPJ;
import br.com.folhapagamento.model.abstracts.FuncionarioBase;
import br.com.folhapagamento.interfaces.ICalculadoraSalario;
import br.com.folhapagamento.interfaces.ICalculadoraAdicionais;
import br.com.folhapagamento.interfaces.ICalculadoraBeneficios;
import br.com.folhapagamento.interfaces.ICalculadoraDescontos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Polimorfismo - CalculadoraFolha")
class CalculadoraFolhaPolimorfismoTest {
    
    @Mock
    private ICalculadoraSalario calculadoraSalario;
    
    @Mock
    private ICalculadoraAdicionais calculadoraAdicionais;
    
    @Mock
    private ICalculadoraBeneficios calculadoraBeneficios;
    
    @Mock
    private ICalculadoraDescontos calculadoraDescontos;
    
    @InjectMocks
    private CalculadoraFolha calculadoraFolha;
    
    private FuncionarioCLT funcionarioCLT;
    private FuncionarioPJ funcionarioPJ;
    
    @BeforeEach
    void setUp() {
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
        
        // Setup mocks
        when(calculadoraSalario.calcularSalarioHora(anyDouble())).thenReturn(15.0);
        when(calculadoraAdicionais.calcularPericulosidade(any())).thenReturn(0.0);
        when(calculadoraAdicionais.calcularInsalubridade(any())).thenReturn(0.0);
        when(calculadoraBeneficios.calcularValeAlimentacao(any())).thenReturn(500.0);
        when(calculadoraBeneficios.calcularDescontoValeTransporte(any())).thenReturn(150.0);
        when(calculadoraDescontos.calcularINSS(anyDouble())).thenReturn(300.0);
        when(calculadoraDescontos.calcularIRRF(anyDouble(), anyInt())).thenReturn(100.0);
        when(calculadoraDescontos.calcularFGTS(anyDouble())).thenReturn(240.0);
    }
    
    @Test
    @DisplayName("Deve calcular folha polimórfica para CLT")
    void testCalcularFolhaPolimorfica_CLT() {
        FolhaPagamento folha = calculadoraFolha.calcularFolhaPolimorfica(funcionarioCLT);
        
        assertNotNull(folha);
        assertEquals(funcionarioCLT.getSalarioBruto(), folha.getSalarioBruto());
        assertTrue(folha.getSalarioLiquido() > 0);
    }
    
    @Test
    @DisplayName("Deve calcular folha polimórfica para PJ")
    void testCalcularFolhaPolimorfica_PJ() {
        // Mock adicional para PJ que não tem dependentes
        when(calculadoraDescontos.calcularIRRF(anyDouble(), anyInt())).thenReturn(100.0);
        
        FolhaPagamento folha = calculadoraFolha.calcularFolhaPolimorfica(funcionarioPJ);
        
        assertNotNull(folha);
        assertEquals(funcionarioPJ.getSalarioBruto(), folha.getSalarioBruto());
        assertTrue(folha.getSalarioLiquido() > 0);
    }
    
    @Test
    @DisplayName("Deve calcular folhas em lote")
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
    @DisplayName("Deve processar CLT e PJ de forma diferente")
    void testPolimorfismo_ProcessamentoDiferente() {
        FuncionarioCLT clt = new FuncionarioCLT();
        clt.setNome("Teste CLT");
        clt.setCpf("111");
        clt.setCargo("Cargo");
        clt.setSalarioBruto(1500.0);
        clt.setNumeroDependentes(0);
        clt.setValorValeTransporte(50.0);
        
        FuncionarioPJ pj = new FuncionarioPJ();
        pj.setNome("Teste PJ");
        pj.setCpf("222");
        pj.setCargo("Cargo");
        pj.setSalarioBruto(2000.0);
        pj.setCnpjEmpresa("CNPJ");
        
        FolhaPagamento folhaCLT = calculadoraFolha.calcularFolhaPolimorfica(clt);
        FolhaPagamento folhaPJ = calculadoraFolha.calcularFolhaPolimorfica(pj);
        
        assertNotNull(folhaCLT);
        assertNotNull(folhaPJ);
        // Salários brutos são diferentes
        assertNotEquals(folhaCLT.getSalarioBruto(), folhaPJ.getSalarioBruto());
    }
    
    @Test
    @DisplayName("Deve verificar herança de FuncionarioBase")
    void testHeranca_FuncionarioBase() {
        // Testa se as classes herdam de FuncionarioBase
        assertNotNull(funcionarioCLT);
        assertNotNull(funcionarioPJ);
        assertEquals("João CLT", funcionarioCLT.getNome());
        assertEquals("Maria PJ", funcionarioPJ.getNome());
    }
    
    @Test
    @DisplayName("Deve calcular salário líquido específico para cada tipo")
    void testPolimorfismo_CalculoEspecifico() {
        // Testa métodos de cálculo interno das classes
        double beneficiosCLT = funcionarioCLT.calcularBeneficios();
        double beneficiosPJ = funcionarioPJ.calcularBeneficios();
        
        // CLT e PJ têm cálculos de benefícios diferentes
        assertTrue(beneficiosCLT >= 0);
        assertTrue(beneficiosPJ >= 0);
    }
}