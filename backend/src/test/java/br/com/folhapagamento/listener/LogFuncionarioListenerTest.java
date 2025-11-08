package br.com.folhapagamento.listener;

import br.com.folhapagamento.event.FuncionarioCadastradoEvent;
import br.com.folhapagamento.model.entity.FuncionarioEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - LogFuncionarioListener")
class LogFuncionarioListenerTest {

    @InjectMocks
    private LogFuncionarioListener listener;
    
    private FuncionarioEntity funcionario;
    
    @BeforeEach
    void setUp() {
        funcionario = new FuncionarioEntity();
        funcionario.setId(1L);
        funcionario.setNome("João Silva");
        funcionario.setCpf("123.456.789-00");
        funcionario.setCargo("Desenvolvedor");
        funcionario.setTipo("CLT");
        funcionario.setSalarioBruto(5000.0);
    }
    
    @Test
    @DisplayName("Deve processar evento de cadastro sem erros")
    void deveProcessarEventoDeCadastro() {
        // Arrange
        FuncionarioCadastradoEvent event = new FuncionarioCadastradoEvent(
            funcionario, 
            "Funcionário cadastrado no sistema"
        );
        
        // Act & Assert - Não deve lançar exceção
        assertDoesNotThrow(() -> listener.aoCadastrarFuncionario(event));
    }
    
    @Test
    @DisplayName("Deve processar evento de atualização sem erros")
    void deveProcessarEventoDeAtualizacao() {
        // Arrange
        FuncionarioCadastradoEvent event = new FuncionarioCadastradoEvent(
            funcionario, 
            "Dados do funcionário atualizados"
        );
        
        // Act & Assert - Não deve lançar exceção
        assertDoesNotThrow(() -> listener.aoCadastrarFuncionario(event));
    }
    
    @Test
    @DisplayName("Deve aceitar evento com diferentes tipos de funcionário")
    void deveAceitarDiferentesTipos() {
        // Arrange
        funcionario.setTipo("PJ");
        FuncionarioCadastradoEvent event = new FuncionarioCadastradoEvent(
            funcionario, 
            "Funcionário PJ cadastrado"
        );
        
        // Act & Assert
        assertDoesNotThrow(() -> listener.aoCadastrarFuncionario(event));
    }
}

