package br.com.folhapagamento.service;

import br.com.folhapagamento.model.FolhaPagamento;
import br.com.folhapagamento.model.entity.FolhaPagamentoEntity;
import br.com.folhapagamento.model.entity.FuncionarioEntity;
import br.com.folhapagamento.repository.FolhaPagamentoRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - FolhaPagamentoService")
class FolhaPagamentoServiceTest {

    @Mock
    private FolhaPagamentoRepository folhaPagamentoRepository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private CalculadoraFolha calculadoraFolha;

    @InjectMocks
    private FolhaPagamentoService folhaPagamentoService;

    private FuncionarioEntity funcionario1;
    private FolhaPagamentoEntity folha1;
    private FolhaPagamentoEntity folha2;
    private FolhaPagamentoEntity folha3;

    @BeforeEach
    void setUp() {
        funcionario1 = criarFuncionario(1L, "João Silva", "123.456.789-00");
        
        folha1 = criarFolhaPagamento(1L, funcionario1, 5000.0, 4500.0, 11, 2024);
        folha2 = criarFolhaPagamento(2L, funcionario1, 6000.0, 5500.0, 11, 2024);
        folha3 = criarFolhaPagamento(3L, funcionario1, 7000.0, 6500.0, 12, 2024);
    }

    @Test
    @DisplayName("Deve salvar folha de pagamento com sucesso")
    void deveSalvarFolhaPagamento() {
        FolhaPagamento folha = criarFolhaPagamentoModel(5000.0, 4500.0);
        
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario1));
        when(folhaPagamentoRepository.save(any(FolhaPagamentoEntity.class)))
                .thenReturn(folha1);

        FolhaPagamentoEntity resultado = folhaPagamentoService.salvar(folha, 1L);

        assertNotNull(resultado);
        assertEquals(5000.0, resultado.getSalarioBruto());
        verify(funcionarioRepository, times(1)).findById(1L);
        verify(folhaPagamentoRepository, times(1)).save(any(FolhaPagamentoEntity.class));
    }

    @Test
    @DisplayName("Deve buscar todas as folhas de pagamento")
    void deveBuscarTodasFolhas() {
        List<FolhaPagamentoEntity> folhas = Arrays.asList(folha1, folha2, folha3);
        
        when(folhaPagamentoRepository.findAll()).thenReturn(folhas);

        List<FolhaPagamentoEntity> resultado = folhaPagamentoService.buscarTodas();

        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        verify(folhaPagamentoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar folha de pagamento por ID")
    void deveBuscarFolhaPorId() {
        when(folhaPagamentoRepository.findById(1L)).thenReturn(Optional.of(folha1));

        Optional<FolhaPagamentoEntity> resultado = folhaPagamentoService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(5000.0, resultado.get().getSalarioBruto());
        verify(folhaPagamentoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve buscar folhas por funcionário")
    void deveBuscarFolhasPorFuncionario() {
        List<FolhaPagamentoEntity> folhas = Arrays.asList(folha1, folha2, folha3);
        
        when(folhaPagamentoRepository.findByFuncionarioId(1L)).thenReturn(folhas);

        List<FolhaPagamentoEntity> resultado = folhaPagamentoService.buscarPorFuncionario(1L);

        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        verify(folhaPagamentoRepository, times(1)).findByFuncionarioId(1L);
    }

    @Test
    @DisplayName("Deve buscar folhas por mês e ano")
    void deveBuscarFolhasPorMesEAno() {
        List<FolhaPagamentoEntity> folhas = Arrays.asList(folha1, folha2);
        
        when(folhaPagamentoRepository.findByMesReferenciaAndAnoReferencia(11, 2024))
                .thenReturn(folhas);

        List<FolhaPagamentoEntity> resultado = folhaPagamentoService.buscarPorMesEAno(11, 2024);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(f -> f.getMesReferencia() == 11 && f.getAnoReferencia() == 2024));
        verify(folhaPagamentoRepository, times(1)).findByMesReferenciaAndAnoReferencia(11, 2024);
    }

    @Test
    @DisplayName("Deve filtrar folhas por faixa de salário líquido")
    void deveFiltrarPorFaixaSalarioLiquido() {
        List<FolhaPagamentoEntity> folhas = Arrays.asList(folha1, folha2);
        
        when(folhaPagamentoRepository.findBySalarioLiquidoBetween(4500.0, 5500.0))
                .thenReturn(folhas);

        List<FolhaPagamentoEntity> resultado = folhaPagamentoService.filtrarPorFaixaSalarioLiquido(4500.0, 5500.0);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(folhaPagamentoRepository, times(1)).findBySalarioLiquidoBetween(4500.0, 5500.0);
    }

    @Test
    @DisplayName("Deve processar e filtrar folhas com múltiplos critérios usando Stream")
    void deveProcessarEFiltrarComMultiplosCriterios() {
        List<FolhaPagamentoEntity> todasFolhas = Arrays.asList(folha1, folha2, folha3);
        
        when(folhaPagamentoRepository.findAllAsStream())
                .thenReturn(todasFolhas.stream());

        List<FolhaPagamentoEntity> resultado = folhaPagamentoService.processarEFiltrar(
                11, 2024, 4500.0, 5500.0, 1L);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(f -> 
                f.getMesReferencia() == 11 && 
                f.getAnoReferencia() == 2024 &&
                f.getSalarioLiquido() >= 4500.0 &&
                f.getSalarioLiquido() <= 5500.0));
        verify(folhaPagamentoRepository, times(1)).findAllAsStream();
    }

    @Test
    @DisplayName("Deve processar e filtrar apenas por mês e ano usando Stream")
    void deveProcessarEFiltrarApenasPorMesEAno() {
        List<FolhaPagamentoEntity> todasFolhas = Arrays.asList(folha1, folha2, folha3);
        
        when(folhaPagamentoRepository.findAllAsStream())
                .thenReturn(todasFolhas.stream());

        List<FolhaPagamentoEntity> resultado = folhaPagamentoService.processarEFiltrar(
                11, 2024, null, null, null);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(f -> 
                f.getMesReferencia() == 11 && f.getAnoReferencia() == 2024));
        verify(folhaPagamentoRepository, times(1)).findAllAsStream();
    }

    @Test
    @DisplayName("Deve processar e filtrar apenas por salário líquido mínimo usando Stream")
    void deveProcessarEFiltrarApenasPorSalarioLiquidoMinimo() {
        List<FolhaPagamentoEntity> todasFolhas = Arrays.asList(folha1, folha2, folha3);
        
        when(folhaPagamentoRepository.findAllAsStream())
                .thenReturn(todasFolhas.stream());

        List<FolhaPagamentoEntity> resultado = folhaPagamentoService.processarEFiltrar(
                null, null, 5500.0, null, null);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(f -> f.getSalarioLiquido() >= 5500.0));
        verify(folhaPagamentoRepository, times(1)).findAllAsStream();
    }

    @Test
    @DisplayName("Deve calcular estatísticas das folhas usando Stream")
    void deveCalcularEstatisticas() {
        List<FolhaPagamentoEntity> folhas = Arrays.asList(folha1, folha2, folha3);
        
        when(folhaPagamentoRepository.findAll()).thenReturn(folhas);

        FolhaPagamentoService.EstatisticasFolhas stats = folhaPagamentoService.calcularEstatisticas(null, null);

        assertNotNull(stats);
        assertEquals(3, stats.getTotalFolhas());
        assertEquals(18000.0, stats.getTotalSalarioBruto());
        assertEquals(16500.0, stats.getTotalSalarioLiquido());
        assertEquals(5500.0, stats.getSalarioLiquidoMedio());
        assertEquals(6500.0, stats.getSalarioLiquidoMaximo());
        assertEquals(4500.0, stats.getSalarioLiquidoMinimo());
        verify(folhaPagamentoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve calcular estatísticas por mês e ano usando Stream")
    void deveCalcularEstatisticasPorMesEAno() {
        List<FolhaPagamentoEntity> folhas = Arrays.asList(folha1, folha2);
        
        when(folhaPagamentoRepository.findByMesReferenciaAndAnoReferencia(11, 2024))
                .thenReturn(folhas);

        FolhaPagamentoService.EstatisticasFolhas stats = folhaPagamentoService.calcularEstatisticas(11, 2024);

        assertNotNull(stats);
        assertEquals(2, stats.getTotalFolhas());
        assertEquals(11000.0, stats.getTotalSalarioBruto());
        assertEquals(10000.0, stats.getTotalSalarioLiquido());
        verify(folhaPagamentoRepository, times(1)).findByMesReferenciaAndAnoReferencia(11, 2024);
    }

    @Test
    @DisplayName("Deve calcular estatísticas com lista vazia")
    void deveCalcularEstatisticasComListaVazia() {
        when(folhaPagamentoRepository.findAll()).thenReturn(Arrays.asList());

        FolhaPagamentoService.EstatisticasFolhas stats = folhaPagamentoService.calcularEstatisticas(null, null);

        assertNotNull(stats);
        assertEquals(0, stats.getTotalFolhas());
        assertEquals(0.0, stats.getTotalSalarioBruto());
        assertEquals(0.0, stats.getTotalSalarioLiquido());
        verify(folhaPagamentoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve agrupar folhas por funcionário usando Stream")
    void deveAgruparPorFuncionario() {
        List<FolhaPagamentoEntity> folhas = Arrays.asList(folha1, folha2, folha3);
        
        when(folhaPagamentoRepository.findAll()).thenReturn(folhas);

        java.util.Map<String, List<FolhaPagamentoEntity>> resultado = folhaPagamentoService.agruparPorFuncionario();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertTrue(resultado.containsKey("João Silva"));
        assertEquals(3, resultado.get("João Silva").size());
        verify(folhaPagamentoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve ordenar folhas por salário líquido ascendente usando Stream")
    void deveOrdenarPorSalarioLiquidoAscendente() {
        List<FolhaPagamentoEntity> folhas = Arrays.asList(folha1, folha2, folha3);
        
        when(folhaPagamentoRepository.findAllAsStream())
                .thenReturn(folhas.stream());

        List<FolhaPagamentoEntity> resultado = folhaPagamentoService.ordenarPorSalarioLiquido(true);

        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        assertEquals(4500.0, resultado.get(0).getSalarioLiquido());
        assertEquals(5500.0, resultado.get(1).getSalarioLiquido());
        assertEquals(6500.0, resultado.get(2).getSalarioLiquido());
        verify(folhaPagamentoRepository, times(1)).findAllAsStream();
    }

    @Test
    @DisplayName("Deve ordenar folhas por salário líquido descendente usando Stream")
    void deveOrdenarPorSalarioLiquidoDescendente() {
        List<FolhaPagamentoEntity> folhas = Arrays.asList(folha1, folha2, folha3);
        
        when(folhaPagamentoRepository.findAllAsStream())
                .thenReturn(folhas.stream());

        List<FolhaPagamentoEntity> resultado = folhaPagamentoService.ordenarPorSalarioLiquido(false);

        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        assertEquals(6500.0, resultado.get(0).getSalarioLiquido());
        assertEquals(5500.0, resultado.get(1).getSalarioLiquido());
        assertEquals(4500.0, resultado.get(2).getSalarioLiquido());
        verify(folhaPagamentoRepository, times(1)).findAllAsStream();
    }

    @Test
    @DisplayName("Deve calcular e salvar folha de pagamento")
    void deveCalcularESalvar() {
        br.com.folhapagamento.model.Funcionario funcionario = criarFuncionarioModel();
        FolhaPagamento folhaCalculada = criarFolhaPagamentoModel(5000.0, 4500.0);
        
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario1));
        when(calculadoraFolha.calcularFolha(any(br.com.folhapagamento.model.Funcionario.class)))
                .thenReturn(folhaCalculada);
        when(folhaPagamentoRepository.save(any(FolhaPagamentoEntity.class)))
                .thenReturn(folha1);

        FolhaPagamentoEntity resultado = folhaPagamentoService.calcularESalvar(1L);

        assertNotNull(resultado);
        verify(funcionarioRepository, times(1)).findById(1L);
        verify(calculadoraFolha, times(1)).calcularFolha(any(br.com.folhapagamento.model.Funcionario.class));
        verify(folhaPagamentoRepository, times(1)).save(any(FolhaPagamentoEntity.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao calcular folha para funcionário inexistente")
    void deveLancarExcecaoAoCalcularFolhaParaFuncionarioInexistente() {
        when(funcionarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            folhaPagamentoService.calcularESalvar(999L);
        });
        
        verify(funcionarioRepository, times(1)).findById(999L);
        verify(calculadoraFolha, never()).calcularFolha(any());
        verify(folhaPagamentoRepository, never()).save(any());
    }

    private FuncionarioEntity criarFuncionario(Long id, String nome, String cpf) {
        FuncionarioEntity entity = new FuncionarioEntity();
        entity.setId(id);
        entity.setNome(nome);
        entity.setCpf(cpf);
        entity.setTipo("CLT");
        entity.setCargo("Desenvolvedor");
        entity.setSalarioBruto(5000.0);
        entity.setNumeroDependentes(0);
        entity.setRecebePericulosidade(false);
        entity.setValorValeTransporte(200.0);
        entity.setValorValeAlimentacao(500.0);
        entity.setDataCriacao(LocalDateTime.now());
        entity.setDataAtualizacao(LocalDateTime.now());
        return entity;
    }

    private FolhaPagamentoEntity criarFolhaPagamento(Long id, FuncionarioEntity funcionario,
                                                     Double salarioBruto, Double salarioLiquido,
                                                     Integer mes, Integer ano) {
        FolhaPagamentoEntity entity = new FolhaPagamentoEntity();
        entity.setId(id);
        entity.setFuncionario(funcionario);
        entity.setSalarioBruto(salarioBruto);
        entity.setSalarioLiquido(salarioLiquido);
        entity.setMesReferencia(mes);
        entity.setAnoReferencia(ano);
        entity.setSalarioPorHora(25.0);
        entity.setAdicionalPericulosidade(0.0);
        entity.setAdicionalInsalubridade(0.0);
        entity.setValeAlimentacao(500.0);
        entity.setDescontoValeTransporte(200.0);
        entity.setDescontoINSS(450.0);
        entity.setDescontoIRRF(0.0);
        entity.setFgts(400.0);
        entity.setTotalAntesDescontos(5500.0);
        entity.setTotalDescontos(500.0);
        entity.setDataCriacao(LocalDateTime.now());
        entity.setDataAtualizacao(LocalDateTime.now());
        return entity;
    }

    private FolhaPagamento criarFolhaPagamentoModel(Double salarioBruto, Double salarioLiquido) {
        FolhaPagamento folha = new FolhaPagamento();
        folha.setSalarioBruto(salarioBruto);
        folha.setSalarioLiquido(salarioLiquido);
        folha.setSalarioPorHora(25.0);
        folha.setAdicionalPericulosidade(0.0);
        folha.setAdicionalInsalubridade(0.0);
        folha.setValeAlimentacao(500.0);
        folha.setDescontoValeTransporte(200.0);
        folha.setDescontoINSS(450.0);
        folha.setDescontoIRRF(0.0);
        folha.setFgts(400.0);
        folha.setTotalAntesDescontos(5500.0);
        folha.setTotalDescontos(500.0);
        return folha;
    }

    private br.com.folhapagamento.model.Funcionario criarFuncionarioModel() {
        br.com.folhapagamento.model.Funcionario funcionario = new br.com.folhapagamento.model.Funcionario();
        funcionario.setNome("João Silva");
        funcionario.setCpf("123.456.789-00");
        funcionario.setTipo("CLT");
        funcionario.setCargo("Desenvolvedor");
        funcionario.setSalarioBruto(5000.0);
        funcionario.setNumeroDependentes(0);
        funcionario.setRecebePericulosidade(false);
        funcionario.setValorValeTransporte(200.0);
        funcionario.setValorValeAlimentacao(500.0);
        return funcionario;
    }
}

