package test.java.br.com.folhapagamento.service.abstracts;

import br.com.folhapagamento.model.Funcionario;
import br.com.folhapagamento.repository.FuncionarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FolhaPagamentoServiceTest {

    @Mock
    private FuncionarioRepository repositoryMock;

    @InjectMocks
    private FolhaPagamentoService service;

    @Test
    void deveAgruparEcalcularTotalPorDepartamentoEmMap() {
        Funcionario f1 = new Funcionario();
        f1.setDepartamento("Vendas");
        f1.setSalarioBruto(1000.0);
        
        Funcionario f2 = new Funcionario();
        f2.setDepartamento("TI");
        f2.setSalarioBruto(4000.0);

        Funcionario f3 = new Funcionario();
        f3.setDepartamento("Vendas");
        f3.setSalarioBruto(1500.0);

        when(repositoryMock.findAll()).thenReturn(List.of(f1, f2, f3));
        
        Map<String, Double> mapa = service.calcularTotalPorDepartamento();

        assertEquals(2, mapa.size());
        assertEquals(2500.0, mapa.get("Vendas"));
        assertEquals(4000.0, mapa.get("TI"));
    }
}