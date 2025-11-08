package br.com.folhapagamento.repository;

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
@DisplayName("Testes de Integração - FuncionarioRepository")
class FuncionarioRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    private FuncionarioEntity funcionario1;
    private FuncionarioEntity funcionario2;
    private FuncionarioEntity funcionario3;

    @BeforeEach
    void setUp() {
        funcionario1 = criarFuncionario("João Silva", "123.456.789-00", "CLT", 
                "Desenvolvedor", 5000.0, 2, false, null);
        funcionario2 = criarFuncionario("Maria Santos", "987.654.321-00", "CLT", 
                "Analista", 6000.0, 1, true, null);
        funcionario3 = criarFuncionario("Pedro Oliveira", "111.222.333-44", "PJ", 
                "Consultor", 8000.0, 0, false, "medio");
        
        entityManager.persistAndFlush(funcionario1);
        entityManager.persistAndFlush(funcionario2);
        entityManager.persistAndFlush(funcionario3);
    }

    @Test
    @DisplayName("Deve buscar funcionário por CPF")
    void deveBuscarPorCpf() {
        Optional<FuncionarioEntity> resultado = funcionarioRepository.findByCpf("123.456.789-00");

        assertTrue(resultado.isPresent());
        assertEquals("João Silva", resultado.get().getNome());
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar CPF inexistente")
    void deveRetornarVazioAoBuscarCpfInexistente() {
        Optional<FuncionarioEntity> resultado = funcionarioRepository.findByCpf("999.999.999-99");

        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Deve buscar funcionários por tipo")
    void deveBuscarPorTipo() {
        List<FuncionarioEntity> resultado = funcionarioRepository.findByTipo("CLT");

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(f -> "CLT".equals(f.getTipo())));
    }

    @Test
    @DisplayName("Deve buscar funcionários por cargo")
    void deveBuscarPorCargo() {
        List<FuncionarioEntity> resultado = funcionarioRepository.findByCargo("Desenvolvedor");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Desenvolvedor", resultado.get(0).getCargo());
    }

    @Test
    @DisplayName("Deve buscar funcionários com salário maior ou igual")
    void deveBuscarPorSalarioMaiorOuIgual() {
        List<FuncionarioEntity> resultado = funcionarioRepository.findBySalarioBrutoGreaterThanEqual(6000.0);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(f -> f.getSalarioBruto() >= 6000.0));
    }

    @Test
    @DisplayName("Deve buscar funcionários por faixa salarial")
    void deveBuscarPorFaixaSalarial() {
        List<FuncionarioEntity> resultado = funcionarioRepository.findBySalarioBrutoBetween(5000.0, 7000.0);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(f -> 
                f.getSalarioBruto() >= 5000.0 && f.getSalarioBruto() <= 7000.0));
    }

    @Test
    @DisplayName("Deve buscar funcionários com dependentes")
    void deveBuscarComDependentes() {
        List<FuncionarioEntity> resultado = funcionarioRepository.findComDependentes();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(f -> f.getNumeroDependentes() > 0));
    }

    @Test
    @DisplayName("Deve buscar funcionários com periculosidade")
    void deveBuscarComPericulosidade() {
        List<FuncionarioEntity> resultado = funcionarioRepository.findComPericulosidade();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertTrue(resultado.stream().allMatch(f -> Boolean.TRUE.equals(f.getRecebePericulosidade())));
    }

    @Test
    @DisplayName("Deve buscar funcionários com insalubridade")
    void deveBuscarComInsalubridade() {
        List<FuncionarioEntity> resultado = funcionarioRepository.findComInsalubridade();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertTrue(resultado.stream().allMatch(f -> 
                f.getGrauInsalubridade() != null && !f.getGrauInsalubridade().isEmpty()));
    }

    @Test
    @DisplayName("Deve retornar Stream de todos os funcionários")
    void deveRetornarStreamDeTodosFuncionarios() {
        List<FuncionarioEntity> resultado = funcionarioRepository.findAllAsStream()
                .collect(Collectors.toList());

        assertNotNull(resultado);
        assertEquals(3, resultado.size());
    }

    @Test
    @DisplayName("Deve retornar Stream de funcionários por tipo")
    void deveRetornarStreamPorTipo() {
        List<FuncionarioEntity> resultado = funcionarioRepository.findByTipoAsStream("CLT")
                .collect(Collectors.toList());

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(f -> "CLT".equals(f.getTipo())));
    }

    @Test
    @DisplayName("Deve retornar Stream de funcionários por salário mínimo")
    void deveRetornarStreamPorSalarioMinimo() {
        List<FuncionarioEntity> resultado = funcionarioRepository.findBySalarioBrutoGreaterThanEqualAsStream(6000.0)
                .collect(Collectors.toList());

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(f -> f.getSalarioBruto() >= 6000.0));
    }

    @Test
    @DisplayName("Deve salvar novo funcionário")
    void deveSalvarNovoFuncionario() {
        FuncionarioEntity novoFuncionario = criarFuncionario("Novo Funcionário", 
                "999.888.777-66", "CLT", "Teste", 3000.0, 0, false, null);

        FuncionarioEntity salvo = funcionarioRepository.save(novoFuncionario);

        assertNotNull(salvo.getId());
        assertEquals("Novo Funcionário", salvo.getNome());
    }

    @Test
    @DisplayName("Deve atualizar funcionário existente")
    void deveAtualizarFuncionario() {
        funcionario1.setSalarioBruto(5500.0);
        
        FuncionarioEntity atualizado = funcionarioRepository.save(funcionario1);

        assertEquals(5500.0, atualizado.getSalarioBruto());
    }

    @Test
    @DisplayName("Deve deletar funcionário")
    void deveDeletarFuncionario() {
        funcionarioRepository.deleteById(funcionario1.getId());

        Optional<FuncionarioEntity> resultado = funcionarioRepository.findById(funcionario1.getId());
        assertFalse(resultado.isPresent());
    }

    private FuncionarioEntity criarFuncionario(String nome, String cpf, String tipo,
                                               String cargo, Double salario, Integer dependentes,
                                               Boolean periculosidade, String insalubridade) {
        FuncionarioEntity entity = new FuncionarioEntity();
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
}

