package br.com.folhapagamento.service;

import br.com.folhapagamento.model.Funcionario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculadoraAdicionaisImplTest {
    
    private CalculadoraAdicionaisImpl calculadora;
    private Funcionario funcionario;
    
    @BeforeEach
    void setUp() {
        calculadora = new CalculadoraAdicionaisImpl();
        funcionario = new Funcionario();
        funcionario.setSalarioBruto(3000.0);
    }
    
    @Test
    void testCalcularPericulosidade_ComPericulosidade() {
        funcionario.setRecebePericulosidade(true);
        
        double resultado = calculadora.calcularPericulosidade(funcionario);
        
        assertEquals(900.0, resultado);
    }
    
    @Test
    void testCalcularPericulosidade_SemPericulosidade() {
        funcionario.setRecebePericulosidade(false);
        
        double resultado = calculadora.calcularPericulosidade(funcionario);
        
        assertEquals(0.0, resultado);
    }
    
    @Test
    void testCalcularInsalubridade_Baixo() {
        funcionario.setGrauInsalubridade("baixo");
        
        double resultado = calculadora.calcularInsalubridade(funcionario);
        
        assertEquals(138.06, resultado);
    }
    
    @Test
    void testCalcularInsalubridade_Medio() {
        funcionario.setGrauInsalubridade("medio");
        
        double resultado = calculadora.calcularInsalubridade(funcionario);
        
        assertEquals(276.12, resultado);
    }
    
    @Test
    void testCalcularInsalubridade_Alto() {
        funcionario.setGrauInsalubridade("alto");
        
        double resultado = calculadora.calcularInsalubridade(funcionario);
        
        assertEquals(552.24, resultado);
    }
    
    @Test
    void testCalcularInsalubridade_SemInsalubridade() {
        funcionario.setGrauInsalubridade("");
        
        double resultado = calculadora.calcularInsalubridade(funcionario);
        
        assertEquals(0.0, resultado);
    }
}
