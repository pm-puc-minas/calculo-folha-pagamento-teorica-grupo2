package br.com.folhapagamento.service;

import br.com.folhapagamento.model.Funcionario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculadoraDescontosImplTest {
    
    private CalculadoraDescontosImpl calculadora;
    private Funcionario funcionario;
    
    @BeforeEach
    void setUp() {
        calculadora = new CalculadoraDescontosImpl();
        funcionario = new Funcionario();
        funcionario.setNome("João Silva");
        funcionario.setCpf("12345678901");
        funcionario.setCargo("Desenvolvedor");
        funcionario.setSalarioBruto(3000.0);
        funcionario.setNumeroDependentes(1);
    }
    
    @Test
    void testCalcularINSS_Faixa1() {
        double resultado = calculadora.calcularINSS(1000.0);
        assertEquals(1000.0 * 0.075, resultado, 0.01);
    }
    
    @Test
    void testCalcularINSS_Faixa2() {
        double resultado = calculadora.calcularINSS(2000.0);
        double esperado = (1320.0 * 0.075) + ((2000.0 - 1320.0) * 0.09);
        assertEquals(esperado, resultado, 0.01);
    }
    
    @Test
    void testCalcularINSS_Faixa3() {
        double resultado = calculadora.calcularINSS(3000.0);
        double faixa1 = 1320.0 * 0.075;
        double faixa2 = (2571.29 - 1320.0) * 0.09;
        double faixa3 = (3000.0 - 2571.29) * 0.12;
        double esperado = faixa1 + faixa2 + faixa3;
        assertEquals(esperado, resultado, 0.01);
    }
    
    @Test
    void testCalcularINSS_Faixa4() {
        double resultado = calculadora.calcularINSS(5000.0);
        double faixa1 = 1320.0 * 0.075;
        double faixa2 = (2571.29 - 1320.0) * 0.09;
        double faixa3 = (3856.94 - 2571.29) * 0.12;
        double faixa4 = (5000.0 - 3856.94) * 0.14;
        double esperado = faixa1 + faixa2 + faixa3 + faixa4;
        assertEquals(esperado, resultado, 0.01);
    }
    
    @Test
    void testCalcularIRRF_Isento() {
        double resultado = calculadora.calcularIRRF(2000.0, 0);
        assertEquals(0.0, resultado);
    }
    
    @Test
    void testCalcularIRRF_Faixa2() {
        double resultado = calculadora.calcularIRRF(3000.0, 0);
        double baseIRRF = 3000.0 - calculadora.calcularINSS(3000.0);
        double esperado = (baseIRRF * 0.075) - 142.80;
        assertEquals(esperado, resultado, 0.01);
    }
    
    @Test
    void testCalcularIRRF_ComDependentes() {
        double resultado = calculadora.calcularIRRF(4000.0, 2);
        double inss = calculadora.calcularINSS(4000.0);
        double baseIRRF = 4000.0 - inss - (2 * 189.59);
        double esperado = (baseIRRF * 0.15) - 354.80;
        assertEquals(esperado, resultado, 0.01);
    }
    
    @Test
    void testCalcularFGTS() {
        double resultado = calculadora.calcularFGTS(3000.0);
        assertEquals(3000.0 * 0.08, resultado);
    }
    
    @Test
    void testExecutarCalculo() {
        double resultado = calculadora.executarCalculo(funcionario);
        double inss = calculadora.calcularINSS(funcionario.getSalarioBruto());
        double irrf = calculadora.calcularIRRF(funcionario.getSalarioBruto(), funcionario.getNumeroDependentes());
        assertEquals(inss + irrf, resultado, 0.01);
    }
    
    @Test
    void testGetNomeCalculadora() {
        assertEquals("Calculadora de Descontos", calculadora.getNomeCalculadora());
    }
}