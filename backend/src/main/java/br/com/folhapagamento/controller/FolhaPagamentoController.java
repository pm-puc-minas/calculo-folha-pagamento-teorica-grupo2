package br.com.folhapagamento.controller;

import br.com.folhapagamento.model.FolhaPagamento;
import br.com.folhapagamento.model.Funcionario;
import br.com.folhapagamento.interfaces.FolhaPagamentoService;
import br.com.folhapagamento.model.entity.FolhaPagamentoEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/folhas")
@Tag(name = "Folha de Pagamento", description = "API para cálculo e gerenciamento de folha de pagamento com Streams e persistência")
public class FolhaPagamentoController {
    
    @Autowired
    private br.com.folhapagamento.interfaces.FolhaPagamentoService folhaPagamentoService;
    
    @Autowired
    private br.com.folhapagamento.service.FolhaPagamentoService folhaPagamentoServiceEntity;
    
    @PostMapping("/calcular")
    @Operation(summary = "Calcular folha de pagamento", description = "Calcula a folha de pagamento completa de um funcionário")
    @ApiResponse(responseCode = "200", description = "Folha calculada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    public ResponseEntity<FolhaPagamento> calcularFolha(@Valid @RequestBody Funcionario funcionario) {
        FolhaPagamento folha = folhaPagamentoService.calcularFolha(funcionario);
        return ResponseEntity.ok(folha);
    }
    
    @PostMapping("/calcular-e-salvar/{funcionarioId}")
    @Operation(summary = "Calcular e salvar folha", description = "Calcula e salva a folha de pagamento no banco de dados")
    @ApiResponse(responseCode = "201", description = "Folha calculada e salva com sucesso")
    @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    public ResponseEntity<FolhaPagamentoEntity> calcularESalvar(@PathVariable Long funcionarioId) {
        try {
            FolhaPagamentoEntity entity = folhaPagamentoServiceEntity.calcularESalvar(funcionarioId);
            return ResponseEntity.status(HttpStatus.CREATED).body(entity);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{funcionarioId}")
    @Operation(summary = "Salvar folha de pagamento", description = "Salva uma folha de pagamento calculada no banco de dados")
    @ApiResponse(responseCode = "201", description = "Folha salva com sucesso")
    public ResponseEntity<FolhaPagamentoEntity> salvar(
            @PathVariable Long funcionarioId,
            @Valid @RequestBody FolhaPagamento folha) {
        try {
            FolhaPagamentoEntity entity = folhaPagamentoServiceEntity.salvar(folha, funcionarioId);
            return ResponseEntity.status(HttpStatus.CREATED).body(entity);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping
    @Operation(summary = "Listar todas as folhas", description = "Retorna todas as folhas de pagamento usando Streams")
    public ResponseEntity<List<FolhaPagamentoEntity>> listarTodas() {
        List<FolhaPagamentoEntity> folhas = folhaPagamentoServiceEntity.buscarTodas();
        return ResponseEntity.ok(folhas);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar folha por ID", description = "Retorna uma folha de pagamento específica")
    public ResponseEntity<FolhaPagamentoEntity> buscarPorId(@PathVariable Long id) {
        Optional<FolhaPagamentoEntity> folha = folhaPagamentoServiceEntity.buscarPorId(id);
        return folha.map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/funcionario/{funcionarioId}")
    @Operation(summary = "Buscar folhas por funcionário", description = "Retorna todas as folhas de um funcionário usando Streams")
    public ResponseEntity<List<FolhaPagamentoEntity>> buscarPorFuncionario(@PathVariable Long funcionarioId) {
        List<FolhaPagamentoEntity> folhas = folhaPagamentoServiceEntity.buscarPorFuncionario(funcionarioId);
        return ResponseEntity.ok(folhas);
    }
    
    @GetMapping("/mes/{mes}/ano/{ano}")
    @Operation(summary = "Buscar folhas por mês e ano", description = "Retorna folhas de um mês/ano específico usando Streams")
    public ResponseEntity<List<FolhaPagamentoEntity>> buscarPorMesEAno(
            @PathVariable Integer mes,
            @PathVariable Integer ano) {
        List<FolhaPagamentoEntity> folhas = folhaPagamentoServiceEntity.buscarPorMesEAno(mes, ano);
        return ResponseEntity.ok(folhas);
    }
    
    @GetMapping("/filtro/salario-liquido")
    @Operation(summary = "Filtrar por faixa de salário líquido", description = "Filtra folhas por faixa de salário líquido usando Streams")
    public ResponseEntity<List<FolhaPagamentoEntity>> filtrarPorFaixaSalarioLiquido(
            @RequestParam(required = false) Double salarioMin,
            @RequestParam(required = false) Double salarioMax) {
        List<FolhaPagamentoEntity> folhas;
        if (salarioMin != null && salarioMax != null) {
            folhas = folhaPagamentoServiceEntity.filtrarPorFaixaSalarioLiquido(salarioMin, salarioMax);
        } else if (salarioMin != null) {
            folhas = folhaPagamentoServiceEntity.filtrarPorSalarioLiquidoMinimo(salarioMin);
        } else {
            folhas = folhaPagamentoServiceEntity.buscarTodas();
        }
        return ResponseEntity.ok(folhas);
    }
    
    @GetMapping("/filtro/avancado")
    @Operation(summary = "Filtro avançado", description = "Filtra folhas usando múltiplos critérios com Streams")
    public ResponseEntity<List<FolhaPagamentoEntity>> filtrarAvancado(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Double salarioLiquidoMinimo,
            @RequestParam(required = false) Double salarioLiquidoMaximo,
            @RequestParam(required = false) Long funcionarioId) {
        List<FolhaPagamentoEntity> folhas = folhaPagamentoServiceEntity.processarEFiltrar(
            mes, ano, salarioLiquidoMinimo, salarioLiquidoMaximo, funcionarioId);
        return ResponseEntity.ok(folhas);
    }
    
    @GetMapping("/estatisticas")
    @Operation(summary = "Estatísticas", description = "Calcula estatísticas das folhas usando Streams")
    public ResponseEntity<?> obterEstatisticas(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer ano) {
        br.com.folhapagamento.service.FolhaPagamentoService.EstatisticasFolhas stats = 
            folhaPagamentoServiceEntity.calcularEstatisticas(mes, ano);
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/agrupar/funcionario")
    @Operation(summary = "Agrupar por funcionário", description = "Agrupa folhas por funcionário usando Streams")
    public ResponseEntity<Map<String, List<FolhaPagamentoEntity>>> agruparPorFuncionario() {
        Map<String, List<FolhaPagamentoEntity>> agrupadas = folhaPagamentoServiceEntity.agruparPorFuncionario();
        return ResponseEntity.ok(agrupadas);
    }
    
    @GetMapping("/ordenar/salario-liquido")
    @Operation(summary = "Ordenar por salário líquido", description = "Ordena folhas por salário líquido usando Streams")
    public ResponseEntity<List<FolhaPagamentoEntity>> ordenarPorSalarioLiquido(
            @RequestParam(defaultValue = "true") boolean ascendente) {
        List<FolhaPagamentoEntity> folhas = folhaPagamentoServiceEntity.ordenarPorSalarioLiquido(ascendente);
        return ResponseEntity.ok(folhas);
    }
    
    @GetMapping("/teste")
    @Operation(summary = "Testar API", description = "Verifica se a API está funcionando")
    @ApiResponse(responseCode = "200", description = "API funcionando")
    public String teste() {
        return "API de Folha de Pagamento funcionando!";
    }
}