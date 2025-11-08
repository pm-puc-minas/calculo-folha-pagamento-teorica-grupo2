package br.com.folhapagamento.repository;

import br.com.folhapagamento.model.entity.FolhaPagamentoEntity;
import br.com.folhapagamento.model.entity.FuncionarioEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Testes de Integração - FolhaPagamentoRepository")
class FolhaPagamentoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FolhaPagamentoRepository folhaPagamentoRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    private FuncionarioEntity funcionario1;
    private FolhaPagamentoEntity folha1;
    private FolhaPagamentoEntity folha2;
    private FolhaPagamentoEntity folha3;

    @BeforeEach
    void setUp() {
        funcionario1 = criarFuncionario("João Silva", "123.456.789-00");
        entityManager.persistAndFlush(funcionario1);
        
        folha1 = criarFolhaPagamento(funcionario1, 5000.0, 4500.0, 11, 2024);
        folha2 = criarFolhaPagamento(funcionario1, 6000.0, 5500.0, 11, 2024);
        folha3 = criarFolhaPagamento(funcionario1, 7000.0, 6500.0, 12, 2024);
        
        entityManager.persistAndFlush(folha1);
        entityManager.persistAndFlush(folha2);
        entityManager.persistAndFlush(folha3);
    }

    @Test
    @DisplayName("Deve buscar folhas por funcionário")
    void deveBuscarPorFuncionario() {
        List<FolhaPagamentoEntity> resultado = folhaPagamentoRepository.findByFuncionario(funcionario1);

        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        assertTrue(resultado.stream().allMatch(f -> f.getFuncionario().getId().equals(funcionario1.getId())));
    }

    @Test
    @DisplayName("Deve buscar folhas por ID do funcionário")
    void deveBuscarPorFuncionarioId() {
        List<FolhaPagamentoEntity> resultado = folhaPagamentoRepository.findByFuncionarioId(funcionario1.getId());

        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        assertTrue(resultado.stream().allMatch(f -> f.getFuncionario().getId().equals(funcionario1.getId())));
    }

    @Test
    @DisplayName("Deve buscar folhas por mês e ano")
    void deveBuscarPorMesEAno() {
        List<FolhaPagamentoEntity> resultado = folhaPagamentoRepository.findByMesReferenciaAndAnoReferencia(11, 2024);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(f -> 
                f.getMesReferencia() == 11 && f.getAnoReferencia() == 2024));
    }

    @Test
    @DisplayName("Deve buscar folhas com salário líquido maior ou igual")
    void deveBuscarPorSalarioLiquidoMaiorOuIgual() {
        List<FolhaPagamentoEntity> resultado = folhaPagamentoRepository.findBySalarioLiquidoGreaterThanEqual(5500.0);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(f -> f.getSalarioLiquido() >= 5500.0));
    }

    @Test
    @DisplayName("Deve buscar folhas por faixa de salário líquido")
    void deveBuscarPorFaixaSalarioLiquido() {
        List<FolhaPagamentoEntity> resultado = folhaPagamentoRepository.findBySalarioLiquidoBetween(4500.0, 5500.0);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(f -> 
                f.getSalarioLiquido() >= 4500.0 && f.getSalarioLiquido() <= 5500.0));
    }

    @Test
    @DisplayName("Deve buscar folhas com total de descontos maior que valor")
    void deveBuscarPorTotalDescontos() {
        List<FolhaPagamentoEntity> resultado = folhaPagamentoRepository.findByTotalDescontosGreaterThan(500.0);

        assertNotNull(resultado);
        assertTrue(resultado.stream().allMatch(f -> f.getTotalDescontos() > 500.0));
    }

    @Test
    @DisplayName("Deve retornar Stream de todas as folhas")
    void deveRetornarStreamDeTodasFolhas() {
        List<FolhaPagamentoEntity> resultado = folhaPagamentoRepository.findAllAsStream()
                .collect(Collectors.toList());

        assertNotNull(resultado);
        assertEquals(3, resultado.size());
    }

    @Test
    @DisplayName("Deve retornar Stream de folhas por mês e ano")
    void deveRetornarStreamPorMesEAno() {
        List<FolhaPagamentoEntity> resultado = folhaPagamentoRepository.findByMesAndAnoAsStream(11, 2024)
                .collect(Collectors.toList());

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(f -> 
                f.getMesReferencia() == 11 && f.getAnoReferencia() == 2024));
    }

    @Test
    @DisplayName("Deve retornar Stream de folhas por ID do funcionário")
    void deveRetornarStreamPorFuncionarioId() {
        List<FolhaPagamentoEntity> resultado = folhaPagamentoRepository.findByFuncionarioIdAsStream(funcionario1.getId())
                .collect(Collectors.toList());

        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        assertTrue(resultado.stream().allMatch(f -> f.getFuncionario().getId().equals(funcionario1.getId())));
    }

    @Test
    @DisplayName("Deve salvar nova folha de pagamento")
    void deveSalvarNovaFolha() {
        FolhaPagamentoEntity novaFolha = criarFolhaPagamento(funcionario1, 4000.0, 3500.0, 1, 2025);

        FolhaPagamentoEntity salva = folhaPagamentoRepository.save(novaFolha);

        assertNotNull(salva.getId());
        assertEquals(4000.0, salva.getSalarioBruto());
    }

    @Test
    @DisplayName("Deve atualizar folha de pagamento existente")
    void deveAtualizarFolha() {
        folha1.setSalarioLiquido(4600.0);
        
        FolhaPagamentoEntity atualizada = folhaPagamentoRepository.save(folha1);

        assertEquals(4600.0, atualizada.getSalarioLiquido());
    }

    @Test
    @DisplayName("Deve deletar folha de pagamento")
    void deveDeletarFolha() {
        folhaPagamentoRepository.deleteById(folha1.getId());

        Optional<FolhaPagamentoEntity> resultado = folhaPagamentoRepository.findById(folha1.getId());
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Deve deletar folhas ao deletar funcionário (CASCADE)")
    void deveDeletarFolhasAoDeletarFuncionario() {
        Long funcionarioId = funcionario1.getId();
        
        funcionarioRepository.deleteById(funcionarioId);
        entityManager.flush();
        entityManager.clear();

        List<FolhaPagamentoEntity> folhas = folhaPagamentoRepository.findByFuncionarioId(funcionarioId);
        assertEquals(0, folhas.size());
    }

    private FuncionarioEntity criarFuncionario(String nome, String cpf) {
        FuncionarioEntity entity = new FuncionarioEntity();
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

    private FolhaPagamentoEntity criarFolhaPagamento(FuncionarioEntity funcionario,
                                                     Double salarioBruto, Double salarioLiquido,
                                                     Integer mes, Integer ano) {
        FolhaPagamentoEntity entity = new FolhaPagamentoEntity();
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
        entity.setTotalAntesDescontos(salarioBruto + 500.0);
        entity.setTotalDescontos(salarioBruto - salarioLiquido);
        entity.setDataCriacao(LocalDateTime.now());
        entity.setDataAtualizacao(LocalDateTime.now());
        return entity;
    }
}

