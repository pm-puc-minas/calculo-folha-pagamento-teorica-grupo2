package br.com.folhapagamento.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioPJTest {
    
    private FuncionarioPJ funcionarioPJ;
    
    @BeforeEach
    void setUp() {
        funcionarioPJ = new FuncionarioPJ();
        funcionarioPJ.setNome("Maria Santos");
        funcionarioPJ.setCpf("11144477735");
        funcionarioPJ.setCargo("Consultora");
        funcionarioPJ.setSalarioBruto(5000.0);
        funcionarioPJ.setCnpjEmpresa("12345678000195");
        funcionarioPJ.setPercentualComissao(5.0);
        funcionarioPJ.setRecebeValeAlimentacao(true);
        funcionarioPJ.setValorValeAlimentacao(30.0);
    }
    
    @Test
    void testValidarFuncionario_Valido() {
        assertTrue(funcionarioPJ.validarFuncionario());
    }
    
    @Test
    void testValidarFuncionario_NomeVazio() {
        funcionarioPJ.setNome("");
        assertFalse(funcionarioPJ.validarFuncionario());
    }
    
    @Test
    void testValidarFuncionario_CpfInvalido() {
        funcionarioPJ.setCpf("abc");
        assertFalse(funcionarioPJ.validarFuncionario());
    }
    
    @Test
    void testValidarFuncionario_SalarioBrutoInvalido() {
        funcionarioPJ.setSalarioBruto(0.0);
        assertFalse(funcionarioPJ.validarFuncionario());
    }
    
    @Test
    void testValidarFuncionario_CnpjVazio() {
        funcionarioPJ.setCnpjEmpresa("");
        assertFalse(funcionarioPJ.validarFuncionario());
    }
    
    @Test
    void testCalcularBeneficios() {
        double beneficios = funcionarioPJ.calcularBeneficios();
        assertEquals(660.0, beneficios, 0.01);
    }
    
    @Test
    void testCalcularDescontos() {
        double descontos = funcionarioPJ.calcularDescontos();
        assertEquals(0.0, descontos, 0.01);
    }
    
    @Test
    void testCalcularSalarioLiquido() {
        double salarioLiquido = funcionarioPJ.calcularSalarioLiquido();
        assertEquals(5660.0, salarioLiquido, 0.01);
    }
    
    @Test
    void testCalcularValorTotalComComissoes() {
        funcionarioPJ.setPercentualComissao(8.0);
        double valorTotal = funcionarioPJ.calcularValorTotalComComissoes();
        assertEquals(5400.0, valorTotal, 0.01);
    }
    
    @Test
    void testToString() {
        String resultado = funcionarioPJ.toString();
        
        assertTrue(resultado.contains("Maria Santos"));
        assertTrue(resultado.contains("11144477735"));
        assertTrue(resultado.contains("Consultora"));
        assertTrue(resultado.contains("5000.00"));
        assertTrue(resultado.contains("12345678000195"));
        assertTrue(resultado.contains("5.00%"));
        assertTrue(resultado.contains("Sim"));
    }
    
    @Test
    void testGettersAndSetters() {
        funcionarioPJ.setCnpjEmpresa("98765432000123");
        funcionarioPJ.setPercentualComissao(10.0);
        funcionarioPJ.setRecebeValeAlimentacao(false);
        funcionarioPJ.setValorValeAlimentacao(40.0);
        
        assertEquals("98765432000123", funcionarioPJ.getCnpjEmpresa());
        assertEquals(10.0, funcionarioPJ.getPercentualComissao(), 0.01);
        assertFalse(funcionarioPJ.isRecebeValeAlimentacao());
        assertEquals(40.0, funcionarioPJ.getValorValeAlimentacao(), 0.01);
    }
}