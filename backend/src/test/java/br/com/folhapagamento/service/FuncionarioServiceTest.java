package test.java.br.com.folhapagamento.service;

import br.com.folhapagamento.event.FuncionarioCadastradoEvent;
import br.com.folhapagamento.dto.FuncionarioRequestDTO;
import br.com.folhapagamento.model.Funcionario;
import br.com.folhapagamento.repository.FuncionarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FuncionarioServiceTest {

    @Mock
    private FuncionarioRepository repositoryMock;

    @Mock
    private ApplicationEventPublisher eventPublisherMock;

    @InjectMocks
    private FuncionarioService service;

    @Test
    void deveSalvarFuncionarioEdispararEvento() {
        FuncionarioRequestDTO dto = new FuncionarioRequestDTO("Ana", "Dev", "TI", 5000.0);
        
        Funcionario funcionarioSalvo = new Funcionario();
        funcionarioSalvo.setId(1L);
        funcionarioSalvo.setNome("Ana");

        when(repositoryMock.save(any(Funcionario.class))).thenReturn(funcionarioSalvo);

        Funcionario resultado = service.criarFuncionario(dto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());

        verify(repositoryMock, times(1)).save(any(Funcionario.class));
        verify(eventPublisherMock, times(1)).publishEvent(any(FuncionarioCadastradoEvent.class));
    }
}