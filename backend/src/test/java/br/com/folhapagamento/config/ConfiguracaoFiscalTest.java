package br.com.folhapagamento.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do Singleton ConfiguracaoFiscal")
class ConfiguracaoFiscalTest {
    
    @Test
    @DisplayName("Deve retornar sempre a mesma instância (Singleton)")
    void deveRetornarMesmaInstancia() {
        ConfiguracaoFiscal instancia1 = ConfiguracaoFiscal.getInstance();
        ConfiguracaoFiscal instancia2 = ConfiguracaoFiscal.getInstance();
        
        assertSame(instancia1, instancia2, "Deve ser a mesma instância");
    }
    
    @Test
    @DisplayName("Deve retornar salário mínimo correto")
    void deveRetornarSalarioMinimo() {
        ConfiguracaoFiscal config = ConfiguracaoFiscal.getInstance();
        
        assertEquals(1412.00, config.getSalarioMinimo());
    }
    
    @Test
    @DisplayName("Deve retornar horas mensais corretas")
    void deveRetornarHorasMensais() {
        ConfiguracaoFiscal config = ConfiguracaoFiscal.getInstance();
        
        assertEquals(220, config.getHorasMensais());
    }
    
    @Test
    @DisplayName("Deve retornar percentual FGTS correto")
    void deveRetornarPercentualFgts() {
        ConfiguracaoFiscal config = ConfiguracaoFiscal.getInstance();
        
        assertEquals(0.08, config.getPercentualFgts());
    }
    
    @Test
    @DisplayName("Deve retornar percentual periculosidade correto")
    void deveRetornarPercentualPericulosidade() {
        ConfiguracaoFiscal config = ConfiguracaoFiscal.getInstance();
        
        assertEquals(0.30, config.getPercentualPericulosidade());
    }
    
    @Test
    @DisplayName("Deve retornar limite vale transporte correto")
    void deveRetornarLimiteValeTransporte() {
        ConfiguracaoFiscal config = ConfiguracaoFiscal.getInstance();
        
        assertEquals(0.06, config.getLimiteValeTransporte());
    }
    
    @Test
    @DisplayName("Deve retornar percentual insalubridade MINIMO")
    void deveRetornarInsalubridadeMinimo() {
        ConfiguracaoFiscal config = ConfiguracaoFiscal.getInstance();
        
        assertEquals(0.10, config.getPercentualInsalubridade("MINIMO"));
    }
    
    @Test
    @DisplayName("Deve retornar percentual insalubridade MEDIO")
    void deveRetornarInsalubridadeMedio() {
        ConfiguracaoFiscal config = ConfiguracaoFiscal.getInstance();
        
        assertEquals(0.20, config.getPercentualInsalubridade("MEDIO"));
    }
    
    @Test
    @DisplayName("Deve retornar percentual insalubridade MAXIMO")
    void deveRetornarInsalubridadeMaximo() {
        ConfiguracaoFiscal config = ConfiguracaoFiscal.getInstance();
        
        assertEquals(0.40, config.getPercentualInsalubridade("MAXIMO"));
    }
    
    @Test
    @DisplayName("Deve calcular valor insalubridade corretamente")
    void deveCalcularValorInsalubridade() {
        ConfiguracaoFiscal config = ConfiguracaoFiscal.getInstance();
        
        double valorMinimo = config.calcularInsalubridade("MINIMO");
        double valorMedio = config.calcularInsalubridade("MEDIO");
        double valorMaximo = config.calcularInsalubridade("MAXIMO");
        
        assertEquals(141.20, valorMinimo, 0.01);
        assertEquals(282.40, valorMedio, 0.01);
        assertEquals(564.80, valorMaximo, 0.01);
    }
    
    @Test
    @DisplayName("Deve retornar zero para grau inválido")
    void deveRetornarZeroParaGrauInvalido() {
        ConfiguracaoFiscal config = ConfiguracaoFiscal.getInstance();
        
        assertEquals(0.0, config.getPercentualInsalubridade("INVALIDO"));
    }
    
    @Test
    @DisplayName("Tabela INSS não deve ser nula")
    void tabelaInssNaoDeveSerNula() {
        ConfiguracaoFiscal config = ConfiguracaoFiscal.getInstance();
        
        assertNotNull(config.getTabelaInss());
        assertFalse(config.getTabelaInss().isEmpty());
    }
    
    @Test
    @DisplayName("Tabela IRRF não deve ser nula")
    void tabelaIrrfNaoDeveSerNula() {
        ConfiguracaoFiscal config = ConfiguracaoFiscal.getInstance();
        
        assertNotNull(config.getTabelaIrrf());
        assertFalse(config.getTabelaIrrf().isEmpty());
    }
    
    @Test
    @DisplayName("Deve ser thread-safe")
    void deveSerThreadSafe() throws InterruptedException {
        final ConfiguracaoFiscal[] instancias = new ConfiguracaoFiscal[10];
        Thread[] threads = new Thread[10];
        
        for (int i = 0; i < 10; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                instancias[index] = ConfiguracaoFiscal.getInstance();
            });
        }
        
        for (Thread thread : threads) {
            thread.start();
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Todas as instâncias devem ser a mesma
        for (int i = 1; i < 10; i++) {
            assertSame(instancias[0], instancias[i], 
                "Todas as threads devem obter a mesma instância");
        }
    }
}

