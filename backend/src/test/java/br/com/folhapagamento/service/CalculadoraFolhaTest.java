package br.com.folhapagamento.service;

import br.com.folhapagamento.interfaces.ICalculadoraSalario;
import br.com.folhapagamento.interfaces.ICalculadoraAdicionais;
import br.com.folhapagamento.interfaces.ICalculadoraBeneficios;
import br.com.folhapagamento.interfaces.ICalculadoraDescontos;
import br.com.folhapagamento.model.FolhaPagamento;
import br.com.folhapagamento.model.Funcionario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculadoraFolhaTest {
    
    @Mock
    private ICalculadoraSalario calculadoraSalario;
    
    @Mock
    private ICalculadoraAdicionais calculadoraAdicionais;
    
    @Mock
    private ICalculadoraBeneficios calculadoraBeneficios;
    
    @Mock
    private ICalculadoraDescontos calculadoraDescontos;
    
    @InjectMocks
    private CalculadoraFolha calculadora;
    
    private Funcionario funcionario;
    
    @BeforeEach
    void setUp() {
        funcionario = new Funcionario();
        funcionario.setNome("João Silva");
        funcionario.setSalarioBruto(3000.0);
        funcionario.setNumeroDependentes(1);
        funcionario.setRecebePericulosidade(false);
        funcionario.setGrauInsalubridade("");
        funcionario.setValorValeTransporte(150.0);
        funcionario.setValorValeAlimentacao(25.0);
    }
    
    @Test
    void testCalcularFolha_FuncionarioBasico() {
        when(calculadoraSalario.calcularSalarioHora(3000.0)).thenReturn(15.0);
        when(calculadoraAdicionais.calcularPericulosidade(funcionario)).thenReturn(0.0);
        when(calculadoraAdicionais.calcularInsalubridade(funcionario)).thenReturn(0.0);
        when(calculadoraBeneficios.calcularValeAlimentacao(funcionario)).thenReturn(550.0);
        when(calculadoraBeneficios.calcularDescontoValeTransporte(funcionario)).thenReturn(150.0);
        when(calculadoraDescontos.calcularINSS(3000.0)).thenReturn(270.0);
        when(calculadoraDescontos.calcularIRRF(3000.0, 1)).thenReturn(0.0);
        when(calculadoraDescontos.calcularFGTS(3000.0)).thenReturn(240.0);
        
        FolhaPagamento folha = calculadora.calcularFolha(funcionario);
        
        assertEquals(3000.0, folha.getSalarioBruto());
        assertEquals(15.0, folha.getSalarioPorHora());
        assertEquals(0.0, folha.getAdicionalPericulosidade());
        assertEquals(0.0, folha.getAdicionalInsalubridade());
        assertEquals(270.0, folha.getDescontoINSS());
        assertEquals(240.0, folha.getFgts());
        assertTrue(folha.getSalarioLiquido() > 0);
        
        verify(calculadoraSalario).calcularSalarioHora(3000.0);
        verify(calculadoraAdicionais).calcularPericulosidade(funcionario);
        verify(calculadoraAdicionais).calcularInsalubridade(funcionario);
        verify(calculadoraBeneficios).calcularValeAlimentacao(funcionario);
        verify(calculadoraBeneficios).calcularDescontoValeTransporte(funcionario);
        verify(calculadoraDescontos).calcularINSS(3000.0);
        verify(calculadoraDescontos).calcularIRRF(3000.0, 1);
        verify(calculadoraDescontos).calcularFGTS(3000.0);
    }
}
