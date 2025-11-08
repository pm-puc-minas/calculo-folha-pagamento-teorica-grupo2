package br.com.folhapagamento.model.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - FuncionarioEntity")
class FuncionarioEntityTest {

    private FuncionarioEntity funcionario;

    @BeforeEach
    void setUp() {
        funcionario = new FuncionarioEntity();
    }

    @Test
    @DisplayName("Deve criar funcionário com todos os campos")
    void deveCriarFuncionarioComTodosCampos() {
        funcionario.setId(1L);
        funcionario.setNome("João Silva");
        funcionario.setCpf("123.456.789-00");
        funcionario.setTipo("CLT");
        funcionario.setCargo("Desenvolvedor");
        funcionario.setSalarioBruto(5000.0);
        funcionario.setNumeroDependentes(2);
        funcionario.setRecebePericulosidade(true);
        funcionario.setGrauInsalubridade("medio");
        funcionario.setValorValeTransporte(200.0);
        funcionario.setValorValeAlimentacao(500.0);
        LocalDateTime agora = LocalDateTime.now();
        funcionario.setDataCriacao(agora);
        funcionario.setDataAtualizacao(agora);

        assertEquals(1L, funcionario.getId());
        assertEquals("João Silva", funcionario.getNome());
        assertEquals("123.456.789-00", funcionario.getCpf());
        assertEquals("CLT", funcionario.getTipo());
        assertEquals("Desenvolvedor", funcionario.getCargo());
        assertEquals(5000.0, funcionario.getSalarioBruto());
        assertEquals(2, funcionario.getNumeroDependentes());
        assertTrue(funcionario.getRecebePericulosidade());
        assertEquals("medio", funcionario.getGrauInsalubridade());
        assertEquals(200.0, funcionario.getValorValeTransporte());
        assertEquals(500.0, funcionario.getValorValeAlimentacao());
        assertNotNull(funcionario.getDataCriacao());
        assertNotNull(funcionario.getDataAtualizacao());
    }

    @Test
    @DisplayName("Deve criar funcionário com valores padrão")
    void deveCriarFuncionarioComValoresPadrao() {
        funcionario.setNome("Maria Santos");
        funcionario.setCpf("987.654.321-00");
        funcionario.setTipo("CLT");
        funcionario.setCargo("Analista");
        funcionario.setSalarioBruto(6000.0);

        assertEquals("Maria Santos", funcionario.getNome());
        assertNull(funcionario.getId());
        assertNull(funcionario.getNumeroDependentes());
        assertNull(funcionario.getRecebePericulosidade());
        assertNull(funcionario.getGrauInsalubridade());
    }

    @Test
    @DisplayName("Deve permitir atualizar campos do funcionário")
    void devePermitirAtualizarCampos() {
        funcionario.setNome("João Silva");
        funcionario.setSalarioBruto(5000.0);

        funcionario.setNome("João Silva Atualizado");
        funcionario.setSalarioBruto(6000.0);

        assertEquals("João Silva Atualizado", funcionario.getNome());
        assertEquals(6000.0, funcionario.getSalarioBruto());
    }

    @Test
    @DisplayName("Deve permitir valores nulos em campos opcionais")
    void devePermitirValoresNulosEmCamposOpcionais() {
        funcionario.setNome("Pedro Oliveira");
        funcionario.setCpf(null);
        funcionario.setGrauInsalubridade(null);
        funcionario.setNumeroDependentes(null);

        assertNull(funcionario.getCpf());
        assertNull(funcionario.getGrauInsalubridade());
        assertNull(funcionario.getNumeroDependentes());
    }

    @Test
    @DisplayName("Deve permitir diferentes tipos de funcionário")
    void devePermitirDiferentesTipos() {
        funcionario.setTipo("CLT");
        assertEquals("CLT", funcionario.getTipo());

        funcionario.setTipo("PJ");
        assertEquals("PJ", funcionario.getTipo());
    }

    @Test
    @DisplayName("Deve permitir diferentes graus de insalubridade")
    void devePermitirDiferentesGrausInsalubridade() {
        funcionario.setGrauInsalubridade("minimo");
        assertEquals("minimo", funcionario.getGrauInsalubridade());

        funcionario.setGrauInsalubridade("medio");
        assertEquals("medio", funcionario.getGrauInsalubridade());

        funcionario.setGrauInsalubridade("maximo");
        assertEquals("maximo", funcionario.getGrauInsalubridade());
    }

    @Test
    @DisplayName("Deve atualizar data de atualização")
    void deveAtualizarDataAtualizacao() {
        LocalDateTime dataInicial = LocalDateTime.now();
        funcionario.setDataCriacao(dataInicial);
        funcionario.setDataAtualizacao(dataInicial);

        LocalDateTime dataAtualizada = LocalDateTime.now().plusDays(1);
        funcionario.setDataAtualizacao(dataAtualizada);

        assertEquals(dataInicial, funcionario.getDataCriacao());
        assertEquals(dataAtualizada, funcionario.getDataAtualizacao());
    }
}

