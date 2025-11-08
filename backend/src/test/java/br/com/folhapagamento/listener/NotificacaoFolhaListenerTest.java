package br.com.folhapagamento.listener;

import br.com.folhapagamento.event.FolhaPagamentoGeradaEvent;
import br.com.folhapagamento.model.entity.FolhaPagamentoEntity;
import br.com.folhapagamento.model.entity.FuncionarioEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - NotificacaoFolhaListener")
class NotificacaoFolhaListenerTest {

    @InjectMocks
    private NotificacaoFolhaListener listener;
    
    private FolhaPagamentoEntity folhaPagamento;
    private FuncionarioEntity funcionario;
    
    @BeforeEach
    void setUp() {
        funcionario = new FuncionarioEntity();
        funcionario.setId(1L);
        funcionario.setNome("João Silva");
        funcionario.setCpf("123.456.789-00");
        
        folhaPagamento = new FolhaPagamentoEntity();
        folhaPagamento.setId(10L);
        folhaPagamento.setFuncionario(funcionario);
        folhaPagamento.setSalarioBruto(5000.0);
        folhaPagamento.setSalarioLiquido(4200.0);
        folhaPagamento.setTotalDescontos(800.0);
        folhaPagamento.setMesReferencia(11);
        folhaPagamento.setAnoReferencia(2024);
    }
    
    @Test
    @DisplayName("Deve processar evento de geração de folha sem erros")
    void deveProcessarEventoDeGeracaoDeFolha() {
        // Arrange
        FolhaPagamentoGeradaEvent event = new FolhaPagamentoGeradaEvent(
            folhaPagamento,
            "Folha de pagamento gerada com sucesso"
        );
        
        // Act & Assert - Não deve lançar exceção
        assertDoesNotThrow(() -> listener.aoGerarFolhaPagamento(event));
    }
    
    @Test
    @DisplayName("Deve processar evento com diferentes valores de salário")
    void deveProcessarDiferentesValores() {
        // Arrange
        folhaPagamento.setSalarioBruto(10000.0);
        folhaPagamento.setSalarioLiquido(8000.0);
        FolhaPagamentoGeradaEvent event = new FolhaPagamentoGeradaEvent(
            folhaPagamento,
            "Folha de pagamento de alto valor gerada"
        );
        
        // Act & Assert
        assertDoesNotThrow(() -> listener.aoGerarFolhaPagamento(event));
    }
    
    @Test
    @DisplayName("Deve processar evento com informações de mês/ano")
    void deveProcessarComMesEAno() {
        // Arrange
        folhaPagamento.setMesReferencia(12);
        folhaPagamento.setAnoReferencia(2025);
        FolhaPagamentoGeradaEvent event = new FolhaPagamentoGeradaEvent(
            folhaPagamento,
            "Folha de pagamento de dezembro/2025 gerada"
        );
        
        // Act & Assert
        assertDoesNotThrow(() -> listener.aoGerarFolhaPagamento(event));
    }
}

