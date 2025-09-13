package br.com.folhapagamento.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GrauInsalubridadeTest {
    
    @Test
    void testFromString_Baixo() {
        GrauInsalubridade grau = GrauInsalubridade.fromString("baixo");
        
        assertEquals(GrauInsalubridade.BAIXO, grau);
        assertEquals("baixo", grau.getValor());
        assertEquals(0.10, grau.getPercentual());
    }
    
    @Test
    void testFromString_Medio() {
        GrauInsalubridade grau = GrauInsalubridade.fromString("medio");
        
        assertEquals(GrauInsalubridade.MEDIO, grau);
        assertEquals("medio", grau.getValor());
        assertEquals(0.20, grau.getPercentual());
    }
    
    @Test
    void testFromString_Alto() {
        GrauInsalubridade grau = GrauInsalubridade.fromString("alto");
        
        assertEquals(GrauInsalubridade.ALTO, grau);
        assertEquals("alto", grau.getValor());
        assertEquals(0.40, grau.getPercentual());
    }
    
    @Test
    void testFromString_ValorInvalido() {
        GrauInsalubridade grau = GrauInsalubridade.fromString("invalido");
        
        assertNull(grau);
    }
    
    @Test
    void testFromString_Null() {
        GrauInsalubridade grau = GrauInsalubridade.fromString(null);
        
        assertNull(grau);
    }
}
