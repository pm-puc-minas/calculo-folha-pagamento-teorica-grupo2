package br.com.folhapagamento.event;

import br.com.folhapagamento.model.entity.FuncionarioEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - FuncionarioCadastradoEvent")
class FuncionarioCadastradoEventTest {

    @Test
    @DisplayName("Deve criar evento com funcionário e ação")
    void deveCriarEventoComFuncionarioEAcao() {
        // Arrange
        FuncionarioEntity funcionario = new FuncionarioEntity();
        funcionario.setId(1L);
        funcionario.setNome("João Silva");
        funcionario.setCpf("123.456.789-00");
        funcionario.setCargo("Desenvolvedor");
        
        String acao = "Funcionário cadastrado no sistema";
        
        // Act
        FuncionarioCadastradoEvent event = new FuncionarioCadastradoEvent(funcionario, acao);
        
        // Assert
        assertNotNull(event);
        assertNotNull(event.getFuncionario());
        assertEquals(funcionario, event.getFuncionario());
        assertEquals(acao, event.getAcaoRealizada());
        assertEquals("João Silva", event.getFuncionario().getNome());
        assertEquals(1L, event.getFuncionario().getId());
    }
    
    @Test
    @DisplayName("Deve criar evento para atualização de funcionário")
    void deveCriarEventoParaAtualizacao() {
        // Arrange
        FuncionarioEntity funcionario = new FuncionarioEntity();
        funcionario.setId(5L);
        funcionario.setNome("Maria Santos");
        
        String acao = "Dados do funcionário atualizados";
        
        // Act
        FuncionarioCadastradoEvent event = new FuncionarioCadastradoEvent(funcionario, acao);
        
        // Assert
        assertNotNull(event);
        assertEquals("Dados do funcionário atualizados", event.getAcaoRealizada());
        assertEquals("Maria Santos", event.getFuncionario().getNome());
    }
}

