package br.com.folhapagamento.service;

import br.com.folhapagamento.model.Funcionario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculadoraBeneficiosImplTest {
    
    private CalculadoraBeneficiosImpl calculadora;
    private Funcionario funcionario;
    
    @BeforeEach
    void setUp() {
        calculadora = new CalculadoraBeneficiosImpl();
        funcionario = new Funcionario();
        funcionario.setNome("João Silva");
        funcionario.setCpf("12345678901");
        funcionario.setCargo("Desenvolvedor");
        funcionario.setSalarioBruto(3000.0);
        funcionario.setValorValeTransporte(150.0);
        funcionario.setValorValeAlimentacao(25.0);
    }
    
    @Test
    void testCalcularValeAlimentacao() {
        double resultado = calculadora.calcularValeAlimentacao(funcionario);
        assertEquals(550.0, resultado, 0.01);
    }
    
    @Test
    void testCalcularValeAlimentacao_SemValor() {
        funcionario.setValorValeAlimentacao(0.0);
        double resultado = calculadora.calcularValeAlimentacao(funcionario);
        assertEquals(0.0, resultado, 0.01);
    }
    
    @Test
    void testCalcularDescontoValeTransporte() {
        double resultado = calculadora.calcularDescontoValeTransporte(funcionario);
        assertEquals(150.0, resultado, 0.01);
    }
    
    @Test
    void testCalcularDescontoValeTransporte_Limite6Porcento() {
        funcionario.setValorValeTransporte(200.0);
        double resultado = calculadora.calcularDescontoValeTransporte(funcionario);
        double limite = funcionario.getSalarioBruto() * 0.06;
        assertEquals(Math.min(200.0, limite), resultado, 0.01);
    }
    
    @Test
    void testCalcularDescontoValeTransporte_SemValor() {
        funcionario.setValorValeTransporte(0.0);
        double resultado = calculadora.calcularDescontoValeTransporte(funcionario);
        assertEquals(0.0, resultado, 0.01);
    }
    
    @Test
    void testExecutarCalculo() {
        double resultado = calculadora.executarCalculo(funcionario);
        double valeAlimentacao = calculadora.calcularValeAlimentacao(funcionario);
        double descontoVT = calculadora.calcularDescontoValeTransporte(funcionario);
        assertEquals(valeAlimentacao - descontoVT, resultado, 0.01);
    }
    
    @Test
    void testGetNomeCalculadora() {
        assertEquals("Calculadora de Benefícios", calculadora.getNomeCalculadora());
    }
}