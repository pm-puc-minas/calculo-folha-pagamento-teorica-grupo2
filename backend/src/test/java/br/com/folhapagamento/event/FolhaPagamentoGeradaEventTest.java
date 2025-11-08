package br.com.folhapagamento.event;

import br.com.folhapagamento.model.entity.FolhaPagamentoEntity;
import br.com.folhapagamento.model.entity.FuncionarioEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - FolhaPagamentoGeradaEvent")
class FolhaPagamentoGeradaEventTest {

    @Test
    @DisplayName("Deve criar evento com folha de pagamento e mensagem")
    void deveCriarEventoComFolhaEMensagem() {
        // Arrange
        FuncionarioEntity funcionario = new FuncionarioEntity();
        funcionario.setId(1L);
        funcionario.setNome("João Silva");
        
        FolhaPagamentoEntity folha = new FolhaPagamentoEntity();
        folha.setId(10L);
        folha.setFuncionario(funcionario);
        folha.setSalarioBruto(5000.0);
        folha.setSalarioLiquido(4200.0);
        folha.setTotalDescontos(800.0);
        folha.setMesReferencia(11);
        folha.setAnoReferencia(2024);
        
        String mensagem = "Folha de pagamento gerada para João Silva - Salário Líquido: R$ 4200.00";
        
        // Act
        FolhaPagamentoGeradaEvent event = new FolhaPagamentoGeradaEvent(folha, mensagem);
        
        // Assert
        assertNotNull(event);
        assertNotNull(event.getFolhaPagamento());
        assertEquals(folha, event.getFolhaPagamento());
        assertEquals(mensagem, event.getMensagem());
        assertEquals(4200.0, event.getFolhaPagamento().getSalarioLiquido());
        assertEquals("João Silva", event.getFolhaPagamento().getFuncionario().getNome());
    }
    
    @Test
    @DisplayName("Deve criar evento com informações completas da folha")
    void deveCriarEventoComInformacoesCompletas() {
        // Arrange
        FuncionarioEntity funcionario = new FuncionarioEntity();
        funcionario.setId(2L);
        funcionario.setNome("Maria Santos");
        
        FolhaPagamentoEntity folha = new FolhaPagamentoEntity();
        folha.setId(20L);
        folha.setFuncionario(funcionario);
        folha.setSalarioBruto(8000.0);
        folha.setSalarioLiquido(6500.0);
        folha.setDescontoINSS(600.0);
        folha.setDescontoIRRF(900.0);
        folha.setFgts(640.0);
        
        String mensagem = "Folha processada com sucesso";
        
        // Act
        FolhaPagamentoGeradaEvent event = new FolhaPagamentoGeradaEvent(folha, mensagem);
        
        // Assert
        assertNotNull(event);
        assertEquals(8000.0, event.getFolhaPagamento().getSalarioBruto());
        assertEquals(6500.0, event.getFolhaPagamento().getSalarioLiquido());
        assertEquals(600.0, event.getFolhaPagamento().getDescontoINSS());
        assertEquals(640.0, event.getFolhaPagamento().getFgts());
    }
}

