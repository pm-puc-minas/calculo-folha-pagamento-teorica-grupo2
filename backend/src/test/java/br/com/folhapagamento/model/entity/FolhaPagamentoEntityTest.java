package br.com.folhapagamento.model.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - FolhaPagamentoEntity")
class FolhaPagamentoEntityTest {

    private FolhaPagamentoEntity folha;
    private FuncionarioEntity funcionario;

    @BeforeEach
    void setUp() {
        funcionario = new FuncionarioEntity();
        funcionario.setId(1L);
        funcionario.setNome("João Silva");
        funcionario.setCpf("123.456.789-00");
        funcionario.setTipo("CLT");
        funcionario.setCargo("Desenvolvedor");
        funcionario.setSalarioBruto(5000.0);

        folha = new FolhaPagamentoEntity();
    }

    @Test
    @DisplayName("Deve criar folha de pagamento com todos os campos")
    void deveCriarFolhaComTodosCampos() {
        folha.setId(1L);
        folha.setFuncionario(funcionario);
        folha.setSalarioBruto(5000.0);
        folha.setSalarioPorHora(25.0);
        folha.setAdicionalPericulosidade(1500.0);
        folha.setAdicionalInsalubridade(276.12);
        folha.setValeAlimentacao(660.0);
        folha.setDescontoValeTransporte(200.0);
        folha.setDescontoINSS(450.0);
        folha.setDescontoIRRF(245.20);
        folha.setFgts(422.09);
        folha.setTotalAntesDescontos(5936.12);
        folha.setTotalDescontos(895.20);
        folha.setSalarioLiquido(5040.92);
        folha.setMesReferencia(11);
        folha.setAnoReferencia(2024);
        LocalDateTime agora = LocalDateTime.now();
        folha.setDataCriacao(agora);
        folha.setDataAtualizacao(agora);

        assertEquals(1L, folha.getId());
        assertEquals(funcionario, folha.getFuncionario());
        assertEquals(5000.0, folha.getSalarioBruto());
        assertEquals(25.0, folha.getSalarioPorHora());
        assertEquals(1500.0, folha.getAdicionalPericulosidade());
        assertEquals(276.12, folha.getAdicionalInsalubridade());
        assertEquals(660.0, folha.getValeAlimentacao());
        assertEquals(200.0, folha.getDescontoValeTransporte());
        assertEquals(450.0, folha.getDescontoINSS());
        assertEquals(245.20, folha.getDescontoIRRF());
        assertEquals(422.09, folha.getFgts());
        assertEquals(5936.12, folha.getTotalAntesDescontos());
        assertEquals(895.20, folha.getTotalDescontos());
        assertEquals(5040.92, folha.getSalarioLiquido());
        assertEquals(11, folha.getMesReferencia());
        assertEquals(2024, folha.getAnoReferencia());
        assertNotNull(folha.getDataCriacao());
        assertNotNull(folha.getDataAtualizacao());
    }

    @Test
    @DisplayName("Deve criar folha de pagamento com valores mínimos")
    void deveCriarFolhaComValoresMinimos() {
        folha.setFuncionario(funcionario);
        folha.setSalarioBruto(1000.0);
        folha.setSalarioPorHora(5.0);
        folha.setAdicionalPericulosidade(0.0);
        folha.setAdicionalInsalubridade(0.0);
        folha.setValeAlimentacao(0.0);
        folha.setDescontoValeTransporte(0.0);
        folha.setDescontoINSS(75.0);
        folha.setDescontoIRRF(0.0);
        folha.setFgts(80.0);
        folha.setTotalAntesDescontos(1000.0);
        folha.setTotalDescontos(75.0);
        folha.setSalarioLiquido(925.0);
        folha.setMesReferencia(1);
        folha.setAnoReferencia(2024);

        assertEquals(1000.0, folha.getSalarioBruto());
        assertEquals(925.0, folha.getSalarioLiquido());
        assertEquals(1, folha.getMesReferencia());
        assertEquals(2024, folha.getAnoReferencia());
    }

    @Test
    @DisplayName("Deve permitir atualizar campos da folha")
    void devePermitirAtualizarCampos() {
        folha.setSalarioLiquido(5000.0);
        folha.setSalarioLiquido(6000.0);

        assertEquals(6000.0, folha.getSalarioLiquido());
    }

    @Test
    @DisplayName("Deve permitir diferentes meses e anos")
    void devePermitirDiferentesMesesEAnos() {
        folha.setMesReferencia(1);
        folha.setAnoReferencia(2024);
        assertEquals(1, folha.getMesReferencia());
        assertEquals(2024, folha.getAnoReferencia());

        folha.setMesReferencia(12);
        folha.setAnoReferencia(2025);
        assertEquals(12, folha.getMesReferencia());
        assertEquals(2025, folha.getAnoReferencia());
    }

    @Test
    @DisplayName("Deve manter relacionamento com funcionário")
    void deveManterRelacionamentoComFuncionario() {
        folha.setFuncionario(funcionario);

        assertEquals(funcionario, folha.getFuncionario());
        assertEquals(1L, folha.getFuncionario().getId());
        assertEquals("João Silva", folha.getFuncionario().getNome());
    }

    @Test
    @DisplayName("Deve calcular valores corretamente")
    void deveCalcularValoresCorretamente() {
        folha.setSalarioBruto(5000.0);
        folha.setAdicionalPericulosidade(1500.0);
        folha.setAdicionalInsalubridade(276.12);
        folha.setValeAlimentacao(660.0);
        folha.setDescontoValeTransporte(200.0);
        folha.setDescontoINSS(450.0);
        folha.setDescontoIRRF(245.20);
        folha.setFgts(422.09);
        folha.setTotalAntesDescontos(5936.12);
        folha.setTotalDescontos(895.20);
        folha.setSalarioLiquido(5040.92);

        double totalEsperado = 5000.0 + 1500.0 + 276.12 + 660.0;
        double descontosEsperados = 200.0 + 450.0 + 245.20;
        double salarioLiquidoEsperado = totalEsperado - descontosEsperados;

        assertEquals(5936.12, folha.getTotalAntesDescontos());
        assertEquals(895.20, folha.getTotalDescontos());
        assertEquals(5040.92, folha.getSalarioLiquido());
    }

    @Test
    @DisplayName("Deve atualizar data de atualização")
    void deveAtualizarDataAtualizacao() {
        LocalDateTime dataInicial = LocalDateTime.now();
        folha.setDataCriacao(dataInicial);
        folha.setDataAtualizacao(dataInicial);

        LocalDateTime dataAtualizada = LocalDateTime.now().plusDays(1);
        folha.setDataAtualizacao(dataAtualizada);

        assertEquals(dataInicial, folha.getDataCriacao());
        assertEquals(dataAtualizada, folha.getDataAtualizacao());
    }
}

