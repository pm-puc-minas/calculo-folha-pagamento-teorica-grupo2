package br.com.folhapagamento.service.abstracts;

import br.com.folhapagamento.model.Funcionario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculadoraBaseTest {
    
    private CalculadoraBase calculadora;
    private Funcionario funcionario;
    
    @BeforeEach
    void setUp() {
        calculadora = new CalculadoraBase() {
            @Override
            protected double executarCalculo(Funcionario funcionario) {
                return funcionario.getSalarioBruto() * 0.1;
            }
            
            @Override
            public String getNomeCalculadora() {
                return "Calculadora Teste";
            }
        };
        
        funcionario = new Funcionario();
        funcionario.setNome("João Silva");
        funcionario.setCpf("12345678901");
        funcionario.setCargo("Desenvolvedor");
        funcionario.setSalarioBruto(3000.0);
    }
    
    @Test
    void testCalcular() {
        double resultado = calculadora.calcular(funcionario);
        assertEquals(300.0, resultado, 0.01);
    }
    
    @Test
    void testCalcularComFuncionarioNulo() {
        assertThrows(IllegalArgumentException.class, () -> {
            calculadora.calcular(null);
        });
    }
    
    @Test
    void testCalcularComSalarioZero() {
        funcionario.setSalarioBruto(0.0);
        assertThrows(IllegalArgumentException.class, () -> {
            calculadora.calcular(funcionario);
        });
    }
    
    @Test
    void testCalcularComSalarioNegativo() {
        funcionario.setSalarioBruto(-100.0);
        assertThrows(IllegalArgumentException.class, () -> {
            calculadora.calcular(funcionario);
        });
    }
    
    @Test
    void testArredondarMonetario() {
        assertEquals(100.00, calculadora.arredondarMonetario(100.000), 0.001);
        assertEquals(100.50, calculadora.arredondarMonetario(100.500), 0.001);
        assertEquals(100.51, calculadora.arredondarMonetario(100.505), 0.001);
        assertEquals(100.50, calculadora.arredondarMonetario(100.504), 0.001);
        assertEquals(99.99, calculadora.arredondarMonetario(99.994), 0.001);
        assertEquals(100.00, calculadora.arredondarMonetario(99.995), 0.001);
    }
    
    @Test
    void testGetNomeCalculadora() {
        assertEquals("Calculadora Teste", calculadora.getNomeCalculadora());
    }
}