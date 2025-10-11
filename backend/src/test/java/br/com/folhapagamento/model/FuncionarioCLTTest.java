package br.com.folhapagamento.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioCLTTest {
    
    private FuncionarioCLT funcionarioCLT;
    
    @BeforeEach
    void setUp() {
        funcionarioCLT = new FuncionarioCLT();
        funcionarioCLT.setNome("João Silva");
        funcionarioCLT.setCpf("11144477735");
        funcionarioCLT.setCargo("Desenvolvedor");
        funcionarioCLT.setSalarioBruto(3000.0);
        funcionarioCLT.setNumeroDependentes(1);
        funcionarioCLT.setRecebePericulosidade(false);
        funcionarioCLT.setGrauInsalubridade("");
        funcionarioCLT.setValorValeTransporte(150.0);
        funcionarioCLT.setValorValeAlimentacao(25.0);
    }
    
    @Test
    void testValidarFuncionario_Valido() {
        assertTrue(funcionarioCLT.validarFuncionario());
    }
    
    @Test
    void testValidarFuncionario_NomeVazio() {
        funcionarioCLT.setNome("");
        assertFalse(funcionarioCLT.validarFuncionario());
    }
    
    @Test
    void testValidarFuncionario_CpfInvalido() {
        funcionarioCLT.setCpf("123");
        assertFalse(funcionarioCLT.validarFuncionario());
    }
    
    @Test
    void testValidarFuncionario_SalarioBrutoInvalido() {
        funcionarioCLT.setSalarioBruto(0.0);
        assertFalse(funcionarioCLT.validarFuncionario());
    }
    
    @Test
    void testCalcularBeneficios() {
        double beneficios = funcionarioCLT.calcularBeneficios();
        assertEquals(550.0, beneficios, 0.01);
    }
    
    @Test
    void testCalcularDescontos() {
        double descontos = funcionarioCLT.calcularDescontos();
        assertEquals(150.0, descontos, 0.01);
    }
    
    @Test
    void testCalcularSalarioLiquido() {
        double salarioLiquido = funcionarioCLT.calcularSalarioLiquido();
        assertEquals(3400.0, salarioLiquido, 0.01);
    }
    
    @Test
    void testToString() {
        String resultado = funcionarioCLT.toString();
        
        assertTrue(resultado.contains("João Silva"));
        assertTrue(resultado.contains("11144477735"));
        assertTrue(resultado.contains("Desenvolvedor"));
        assertTrue(resultado.contains("3000.00"));
        assertTrue(resultado.contains("1"));
        assertTrue(resultado.contains("Não"));
    }
    
    @Test
    void testGettersAndSetters() {
        funcionarioCLT.setNumeroDependentes(2);
        funcionarioCLT.setRecebePericulosidade(true);
        funcionarioCLT.setGrauInsalubridade("medio");
        funcionarioCLT.setValorValeTransporte(200.0);
        funcionarioCLT.setValorValeAlimentacao(30.0);
        
        assertEquals(2, funcionarioCLT.getNumeroDependentes());
        assertTrue(funcionarioCLT.isRecebePericulosidade());
        assertEquals("medio", funcionarioCLT.getGrauInsalubridade());
        assertEquals(200.0, funcionarioCLT.getValorValeTransporte(), 0.01);
        assertEquals(30.0, funcionarioCLT.getValorValeAlimentacao(), 0.01);
    }
}