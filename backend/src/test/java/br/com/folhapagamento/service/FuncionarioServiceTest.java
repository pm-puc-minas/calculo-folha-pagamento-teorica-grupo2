package br.com.folhapagamento.service;

import br.com.folhapagamento.model.Funcionario;
import br.com.folhapagamento.model.entity.FuncionarioEntity;
import br.com.folhapagamento.repository.FuncionarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - FuncionarioService")
class FuncionarioServiceTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @InjectMocks
    private FuncionarioService funcionarioService;

    private FuncionarioEntity funcionario1;
    private FuncionarioEntity funcionario2;
    private FuncionarioEntity funcionario3;

    @BeforeEach
    void setUp() {
        funcionario1 = criarFuncionario(1L, "João Silva", "123.456.789-00", "CLT", 
                "Desenvolvedor", 5000.0, 2, false, null);
        
        funcionario2 = criarFuncionario(2L, "Maria Santos", "987.654.321-00", "CLT", 
                "Analista", 6000.0, 1, true, null);
        
        funcionario3 = criarFuncionario(3L, "Pedro Oliveira", "111.222.333-44", "PJ", 
                "Consultor", 8000.0, 0, false, "medio");
    }

    @Test
    @DisplayName("Deve salvar funcionário com sucesso")
    void deveSalvarFuncionario() {
        Funcionario funcionario = criarFuncionarioModel("João Silva", "123.456.789-00", 
                "CLT", "Desenvolvedor", 5000.0);
        
        when(funcionarioRepository.save(any(FuncionarioEntity.class)))
                .thenReturn(funcionario1);

        FuncionarioEntity resultado = funcionarioService.salvar(funcionario);

        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
        verify(funcionarioRepository, times(1)).save(any(FuncionarioEntity.class));
    }

    @Test
    @DisplayName("Deve buscar todos os funcionários")
    void deveBuscarTodosFuncionarios() {
        List<FuncionarioEntity> funcionarios = Arrays.asList(funcionario1, funcionario2, funcionario3);
        
        when(funcionarioRepository.findAll()).thenReturn(funcionarios);

        List<FuncionarioEntity> resultado = funcionarioService.buscarTodos();

        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        verify(funcionarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar funcionário por ID")
    void deveBuscarFuncionarioPorId() {
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario1));

        Optional<FuncionarioEntity> resultado = funcionarioService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("João Silva", resultado.get().getNome());
        verify(funcionarioRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve buscar funcionário por CPF")
    void deveBuscarFuncionarioPorCpf() {
        when(funcionarioRepository.findByCpf("123.456.789-00"))
                .thenReturn(Optional.of(funcionario1));

        Optional<FuncionarioEntity> resultado = funcionarioService.buscarPorCpf("123.456.789-00");

        assertTrue(resultado.isPresent());
        assertEquals("João Silva", resultado.get().getNome());
        verify(funcionarioRepository, times(1)).findByCpf("123.456.789-00");
    }

    @Test
    @DisplayName("Deve filtrar funcionários por tipo usando Stream")
    void deveFiltrarPorTipo() {
        List<FuncionarioEntity> funcionarios = Arrays.asList(funcionario1, funcionario2, funcionario3);
        
        when(funcionarioRepository.findByTipo("CLT"))
                .thenReturn(Arrays.asList(funcionario1, funcionario2));

        List<FuncionarioEntity> resultado = funcionarioService.filtrarPorTipo("CLT");

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(f -> "CLT".equals(f.getTipo())));
        verify(funcionarioRepository, times(1)).findByTipo("CLT");
    }

    @Test
    @DisplayName("Deve filtrar funcionários por cargo")
    void deveFiltrarPorCargo() {
        when(funcionarioRepository.findByCargo("Desenvolvedor"))
                .thenReturn(Arrays.asList(funcionario1));

        List<FuncionarioEntity> resultado = funcionarioService.filtrarPorCargo("Desenvolvedor");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Desenvolvedor", resultado.get(0).getCargo());
        verify(funcionarioRepository, times(1)).findByCargo("Desenvolvedor");
    }

    @Test
    @DisplayName("Deve filtrar funcionários por faixa salarial")
    void deveFiltrarPorFaixaSalarial() {
        when(funcionarioRepository.findBySalarioBrutoBetween(5000.0, 7000.0))
                .thenReturn(Arrays.asList(funcionario1, funcionario2));

        List<FuncionarioEntity> resultado = funcionarioService.filtrarPorFaixaSalarial(5000.0, 7000.0);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(funcionarioRepository, times(1)).findBySalarioBrutoBetween(5000.0, 7000.0);
    }

    @Test
    @DisplayName("Deve processar e filtrar funcionários com múltiplos critérios usando Stream")
    void deveProcessarEFiltrarComMultiplosCriterios() {
        List<FuncionarioEntity> todosFuncionarios = Arrays.asList(funcionario1, funcionario2, funcionario3);
        
        when(funcionarioRepository.findAllAsStream())
                .thenReturn(todosFuncionarios.stream());

        List<FuncionarioEntity> resultado = funcionarioService.processarEFiltrar(
                "CLT", 5000.0, true, false);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Maria Santos", resultado.get(0).getNome());
        verify(funcionarioRepository, times(1)).findAllAsStream();
    }

    @Test
    @DisplayName("Deve processar e filtrar apenas por tipo usando Stream")
    void deveProcessarEFiltrarApenasPorTipo() {
        List<FuncionarioEntity> todosFuncionarios = Arrays.asList(funcionario1, funcionario2, funcionario3);
        
        when(funcionarioRepository.findAllAsStream())
                .thenReturn(todosFuncionarios.stream());

        List<FuncionarioEntity> resultado = funcionarioService.processarEFiltrar(
                "PJ", null, null, null);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Pedro Oliveira", resultado.get(0).getNome());
        verify(funcionarioRepository, times(1)).findAllAsStream();
    }

    @Test
    @DisplayName("Deve processar e filtrar apenas por salário mínimo usando Stream")
    void deveProcessarEFiltrarApenasPorSalarioMinimo() {
        List<FuncionarioEntity> todosFuncionarios = Arrays.asList(funcionario1, funcionario2, funcionario3);
        
        when(funcionarioRepository.findAllAsStream())
                .thenReturn(todosFuncionarios.stream());

        List<FuncionarioEntity> resultado = funcionarioService.processarEFiltrar(
                null, 6000.0, null, null);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(f -> f.getSalarioBruto() >= 6000.0));
        verify(funcionarioRepository, times(1)).findAllAsStream();
    }

    @Test
    @DisplayName("Deve calcular estatísticas dos funcionários usando Stream")
    void deveCalcularEstatisticas() {
        List<FuncionarioEntity> funcionarios = Arrays.asList(funcionario1, funcionario2, funcionario3);
        
        when(funcionarioRepository.findAll()).thenReturn(funcionarios);

        FuncionarioService.EstatisticasFuncionarios stats = funcionarioService.calcularEstatisticas();

        assertNotNull(stats);
        assertEquals(3, stats.getTotalFuncionarios());
        assertEquals(6333.33, stats.getSalarioMedio(), 0.01);
        assertEquals(8000.0, stats.getSalarioMaximo());
        assertEquals(5000.0, stats.getSalarioMinimo());
        assertEquals(3, stats.getTotalDependentes());
        assertEquals(1, stats.getFuncionariosComPericulosidade());
        assertEquals(1, stats.getFuncionariosComInsalubridade());
        verify(funcionarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve calcular estatísticas com lista vazia")
    void deveCalcularEstatisticasComListaVazia() {
        when(funcionarioRepository.findAll()).thenReturn(Arrays.asList());

        FuncionarioService.EstatisticasFuncionarios stats = funcionarioService.calcularEstatisticas();

        assertNotNull(stats);
        assertEquals(0, stats.getTotalFuncionarios());
        assertEquals(0.0, stats.getSalarioMedio());
        assertEquals(0.0, stats.getSalarioMaximo());
        assertEquals(0.0, stats.getSalarioMinimo());
        verify(funcionarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve agrupar funcionários por cargo usando Stream")
    void deveAgruparPorCargo() {
        List<FuncionarioEntity> funcionarios = Arrays.asList(funcionario1, funcionario2, funcionario3);
        
        when(funcionarioRepository.findAll()).thenReturn(funcionarios);

        java.util.Map<String, List<FuncionarioEntity>> resultado = funcionarioService.agruparPorCargo();

        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        assertTrue(resultado.containsKey("Desenvolvedor"));
        assertTrue(resultado.containsKey("Analista"));
        assertTrue(resultado.containsKey("Consultor"));
        verify(funcionarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve agrupar funcionários por tipo usando Stream")
    void deveAgruparPorTipo() {
        List<FuncionarioEntity> funcionarios = Arrays.asList(funcionario1, funcionario2, funcionario3);
        
        when(funcionarioRepository.findAll()).thenReturn(funcionarios);

        java.util.Map<String, List<FuncionarioEntity>> resultado = funcionarioService.agruparPorTipo();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.containsKey("CLT"));
        assertTrue(resultado.containsKey("PJ"));
        assertEquals(2, resultado.get("CLT").size());
        assertEquals(1, resultado.get("PJ").size());
        verify(funcionarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve ordenar funcionários por salário ascendente usando Stream")
    void deveOrdenarPorSalarioAscendente() {
        List<FuncionarioEntity> funcionarios = Arrays.asList(funcionario1, funcionario2, funcionario3);
        
        when(funcionarioRepository.findAllAsStream())
                .thenReturn(funcionarios.stream());

        List<FuncionarioEntity> resultado = funcionarioService.ordenarPorSalario(true);

        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        assertEquals(5000.0, resultado.get(0).getSalarioBruto());
        assertEquals(6000.0, resultado.get(1).getSalarioBruto());
        assertEquals(8000.0, resultado.get(2).getSalarioBruto());
        verify(funcionarioRepository, times(1)).findAllAsStream();
    }

    @Test
    @DisplayName("Deve ordenar funcionários por salário descendente usando Stream")
    void deveOrdenarPorSalarioDescendente() {
        List<FuncionarioEntity> funcionarios = Arrays.asList(funcionario1, funcionario2, funcionario3);
        
        when(funcionarioRepository.findAllAsStream())
                .thenReturn(funcionarios.stream());

        List<FuncionarioEntity> resultado = funcionarioService.ordenarPorSalario(false);

        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        assertEquals(8000.0, resultado.get(0).getSalarioBruto());
        assertEquals(6000.0, resultado.get(1).getSalarioBruto());
        assertEquals(5000.0, resultado.get(2).getSalarioBruto());
        verify(funcionarioRepository, times(1)).findAllAsStream();
    }

    @Test
    @DisplayName("Deve atualizar funcionário existente")
    void deveAtualizarFuncionario() {
        Funcionario funcionarioAtualizado = criarFuncionarioModel("João Silva Atualizado", 
                "123.456.789-00", "CLT", "Desenvolvedor Senior", 6000.0);
        
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario1));
        when(funcionarioRepository.save(any(FuncionarioEntity.class))).thenReturn(funcionario1);

        FuncionarioEntity resultado = funcionarioService.atualizar(1L, funcionarioAtualizado);

        assertNotNull(resultado);
        verify(funcionarioRepository, times(1)).findById(1L);
        verify(funcionarioRepository, times(1)).save(any(FuncionarioEntity.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar funcionário inexistente")
    void deveLancarExcecaoAoAtualizarFuncionarioInexistente() {
        Funcionario funcionario = criarFuncionarioModel("João Silva", "123.456.789-00", 
                "CLT", "Desenvolvedor", 5000.0);
        
        when(funcionarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            funcionarioService.atualizar(999L, funcionario);
        });
        
        verify(funcionarioRepository, times(1)).findById(999L);
        verify(funcionarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar funcionário")
    void deveDeletarFuncionario() {
        doNothing().when(funcionarioRepository).deleteById(1L);

        funcionarioService.deletar(1L);

        verify(funcionarioRepository, times(1)).deleteById(1L);
    }

    private FuncionarioEntity criarFuncionario(Long id, String nome, String cpf, String tipo,
                                               String cargo, Double salario, Integer dependentes,
                                               Boolean periculosidade, String insalubridade) {
        FuncionarioEntity entity = new FuncionarioEntity();
        entity.setId(id);
        entity.setNome(nome);
        entity.setCpf(cpf);
        entity.setTipo(tipo);
        entity.setCargo(cargo);
        entity.setSalarioBruto(salario);
        entity.setNumeroDependentes(dependentes);
        entity.setRecebePericulosidade(periculosidade);
        entity.setGrauInsalubridade(insalubridade);
        entity.setValorValeTransporte(200.0);
        entity.setValorValeAlimentacao(500.0);
        entity.setDataCriacao(LocalDateTime.now());
        entity.setDataAtualizacao(LocalDateTime.now());
        return entity;
    }

    private Funcionario criarFuncionarioModel(String nome, String cpf, String tipo,
                                              String cargo, Double salario) {
        Funcionario funcionario = new Funcionario();
        funcionario.setNome(nome);
        funcionario.setCpf(cpf);
        funcionario.setTipo(tipo);
        funcionario.setCargo(cargo);
        funcionario.setSalarioBruto(salario);
        funcionario.setNumeroDependentes(0);
        funcionario.setRecebePericulosidade(false);
        funcionario.setValorValeTransporte(200.0);
        funcionario.setValorValeAlimentacao(500.0);
        return funcionario;
    }
}

