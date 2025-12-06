package br.com.folhapagamento.service.abstracts;

import br.com.folhapagamento.model.Funcionario;
import br.com.folhapagamento.model.entity.FuncionarioEntity;
import br.com.folhapagamento.repository.FuncionarioRepository;
import br.com.folhapagamento.service.FuncionarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Folha de Pagamento - Agrupamento por Departamento")
class FolhaPagamentoServiceTest {

    @Mock
    private FuncionarioRepository repositoryMock;

    @InjectMocks
    private FuncionarioService service;

    @Test
    @DisplayName("Deve agrupar funcionários por tipo e contar")
    void deveAgruparFuncionariosPorTipo() {
        FuncionarioEntity f1 = criarFuncionarioEntity(1L, "João", "CLT", 5000.0);
        FuncionarioEntity f2 = criarFuncionarioEntity(2L, "Maria", "PJ", 8000.0);
        FuncionarioEntity f3 = criarFuncionarioEntity(3L, "Pedro", "CLT", 6000.0);

        when(repositoryMock.findAll()).thenReturn(List.of(f1, f2, f3));
        
        Map<String, List<FuncionarioEntity>> mapa = service.agruparPorTipo();

        assertNotNull(mapa);
        assertEquals(2, mapa.size());
        assertEquals(2, mapa.get("CLT").size());
        assertEquals(1, mapa.get("PJ").size());
    }
    
    private FuncionarioEntity criarFuncionarioEntity(Long id, String nome, String tipo, Double salario) {
        FuncionarioEntity entity = new FuncionarioEntity();
        entity.setId(id);
        entity.setNome(nome);
        entity.setTipo(tipo);
        entity.setCargo("Desenvolvedor");
        entity.setSalarioBruto(salario);
        entity.setNumeroDependentes(0);
        entity.setRecebePericulosidade(false);
        entity.setValorValeTransporte(200.0);
        entity.setValorValeAlimentacao(500.0);
        return entity;
    }
}